#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_root"
pull_images=0
skip_metadata=0
jar_root="${JAR_ROOT:-}"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --pull-images) pull_images=1 ;;
    --skip-metadata) skip_metadata=1 ;;
    --jar-root)
      shift
      [[ $# -gt 0 ]] || { echo "--jar-root requires a path." >&2; exit 1; }
      jar_root="$1"
      ;;
    *) echo "Unknown argument: $1" >&2; exit 1 ;;
  esac
  shift
done

if [[ -z "$jar_root" && -f .env ]]; then
  jar_root="$(grep -m 1 -E '^[[:space:]]*JAR_ROOT=' .env | cut -d= -f2- || true)"
  jar_root="${jar_root#\"}"
  jar_root="${jar_root%\"}"
  jar_root="${jar_root#\'}"
  jar_root="${jar_root%\'}"
fi
if [[ -z "$jar_root" ]]; then
  echo "JAR_ROOT is not set. Configure it in .env or pass --jar-root." >&2
  exit 1
fi
if [[ "$jar_root" != /* ]]; then
  jar_root="$project_root/$jar_root"
fi
export JAR_ROOT="$jar_root"

command -v docker >/dev/null || { echo "Docker CLI was not found in PATH." >&2; exit 1; }
docker info >/dev/null
"$project_root/scripts/verify-offline-jars.sh" "$JAR_ROOT"

profile_args=()
if [[ "$skip_metadata" -eq 0 ]]; then
  profile_args=(--profile metadata)
fi

docker compose "${profile_args[@]}" config --quiet
if [[ "$pull_images" -eq 1 ]]; then
  docker compose "${profile_args[@]}" pull
fi
docker compose "${profile_args[@]}" up -d --remove-orphans --wait
docker compose "${profile_args[@]}" ps -a

echo "Big Data Platform started successfully with JAR_ROOT=$JAR_ROOT."
