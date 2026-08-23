#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
manifest_path="$project_root/config/jars/manifest.tsv"
jar_root="${JAR_ROOT:-${1:-}}"

if [[ -z "$jar_root" ]]; then
	echo "JAR_ROOT is not set. Export JAR_ROOT or pass the target directory as the first argument." >&2
	exit 1
fi
if [[ "$jar_root" != /* ]]; then
	jar_root="$project_root/$jar_root"
fi
command -v curl >/dev/null || { echo "curl is required." >&2; exit 1; }

while IFS=$'\t' read -r scope file version required url purpose; do
	[[ "$scope" == "scope" || -z "$scope" ]] && continue
	target_dir="$jar_root/$scope"
	target_path="$target_dir/$file"
	mkdir -p "$target_dir"
	if [[ -s "$target_path" ]]; then
		echo "SKIP $scope/$file"
		continue
	fi
	echo "GET  $url"
	curl --fail --location --retry 3 --output "$target_path" "$url"
done < "$manifest_path"

JAR_ROOT="$jar_root" "$project_root/scripts/verify-offline-jars.sh"
