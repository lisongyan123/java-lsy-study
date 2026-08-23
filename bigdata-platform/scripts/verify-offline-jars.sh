#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
manifest_path="$project_root/config/jars/manifest.tsv"
checksum_path="$project_root/config/jars/checksums.sha256"
jar_root="${JAR_ROOT:-${1:-}}"

if [[ -z "$jar_root" && -f "$project_root/.env" ]]; then
  jar_root="$(grep -m 1 -E '^[[:space:]]*JAR_ROOT=' "$project_root/.env" | cut -d= -f2- || true)"
  jar_root="${jar_root#\"}"
  jar_root="${jar_root%\"}"
  jar_root="${jar_root#\'}"
  jar_root="${jar_root%\'}"
fi
if [[ -z "$jar_root" ]]; then
  echo "JAR_ROOT is not set. Configure it in .env or pass it as the first argument." >&2
  exit 1
fi
if [[ "$jar_root" != /* ]]; then
  jar_root="$project_root/$jar_root"
fi

echo "JAR root: $jar_root"
issues=0
while IFS=$'\t' read -r scope file version required url purpose; do
  [[ "$scope" == "scope" || -z "$scope" ]] && continue
  relative_path="$scope/$file"
  path="$jar_root/$relative_path"
  if [[ ! -e "$path" ]]; then
    if [[ "$required" == "yes" ]]; then
      echo "MISSING $relative_path" >&2
      issues=1
    else
      echo "OPTIONAL $relative_path"
    fi
  elif [[ ! -s "$path" ]]; then
    echo "EMPTY   $relative_path" >&2
    issues=1
  else
    header="$(head -c 2 "$path" | od -An -t x1 | tr -d '[:space:]')"
    if [[ "$header" != "504b" ]]; then
      echo "NOTJAR  $relative_path" >&2
      issues=1
    else
      echo "OK      $relative_path"
    fi
  fi
done < "$manifest_path"

if [[ -f "$checksum_path" ]]; then
  command -v sha256sum >/dev/null || { echo "sha256sum is required." >&2; exit 1; }
  while read -r expected relative_path; do
    [[ -z "${expected:-}" || "$expected" == \#* ]] && continue
    path="$jar_root/$relative_path"
    if [[ -s "$path" ]]; then
      actual="$(sha256sum "$path" | awk '{print $1}')"
      if [[ "$actual" != "$expected" ]]; then
        echo "HASH    $relative_path" >&2
        issues=1
      fi
    fi
  done < "$checksum_path"
fi

if [[ "$issues" -ne 0 ]]; then
  echo "External JAR verification failed." >&2
  exit 1
fi

echo "All required external JARs are present."
