#!/usr/bin/env bash

set -euo pipefail

MISE_INSTALL_PATH="${HOME}/.local/bin/mise"

echo "==> Checking for existing mise installations..."

# -------------------------------------------------------
# Find every mise executable currently visible on PATH
# -------------------------------------------------------
mapfile -t PATH_MISES < <(type -a -p mise 2>/dev/null | sort -u || true)

if [[ ${#PATH_MISES[@]} -gt 0 ]]; then
    echo
    echo "Found mise executable(s) on PATH:"
    for mise_path in "${PATH_MISES[@]}"; do
        echo "  - ${mise_path}"

        if [[ -x "$mise_path" ]]; then
            echo "    version: $("$mise_path" --version 2>/dev/null || echo "unknown")"
        fi
    done
fi

# -------------------------------------------------------
# Check the default mise installation location
# -------------------------------------------------------
if [[ -x "$MISE_INSTALL_PATH" ]]; then
    echo
    echo "Found mise at the default install location:"
    echo "  ${MISE_INSTALL_PATH}"
    echo "  version: $("$MISE_INSTALL_PATH" --version 2>/dev/null || echo "unknown")"
fi

# -------------------------------------------------------
# Check common package-manager installations
# -------------------------------------------------------
echo
echo "==> Checking common package-manager locations..."

for path in \
    /usr/bin/mise \
    /usr/local/bin/mise \
    /opt/homebrew/bin/mise \
    "${HOME}/.cargo/bin/mise"
do
    if [[ -x "$path" ]]; then
        echo "  Found: $path"
        echo "    version: $("$path" --version 2>/dev/null || echo "unknown")"
    fi
done

# ------------------------------------------------------------
# Don't install another copy if mise is already available...
# ------------------------------------------------------------
if command -v mise >/dev/null 2>&1; then
    echo
    echo "==> mise is already installed."
    echo "    Location: $(command -v mise)"
    echo "    Version:  $(mise --version)"

    echo
    echo "Nothing to install."
    exit 0
fi

# -------------------------------------------------------
# Check the default location even if it isn't on PATH
# -------------------------------------------------------
if [[ -x "$MISE_INSTALL_PATH" ]]; then
    echo
    echo "==> mise exists but is not currently on PATH."
    echo "    Location: ${MISE_INSTALL_PATH}"
    echo
    echo "Add this to your shell configuration:"
    echo
    echo 'export PATH="$HOME/.local/bin:$PATH"'
    echo
    exit 1
fi

echo
echo "==> No mise installation found."
echo "==> Installing mise..."

curl -fsSL https://mise.run/bash | sh

echo
echo "==> Verifying installation..."


export PATH="${HOME}/.local/bin:${PATH}"

if ! command -v mise >/dev/null 2>&1; then
    echo "ERROR: mise was installed but could not be found on PATH."
    echo
    echo "Try:"
    echo '  export PATH="$HOME/.local/bin:$PATH"'
    echo '  mise --version'
    exit 1
fi

echo
echo "mise installed successfully!"
echo "  Location: $(command -v mise)"
echo "  Version:  $(mise --version)"

echo
echo "==> Shell activation:"
echo "The Bash installer has configured ~/.bashrc for mise activation."
echo
echo "Restart your shell or run:"
echo "  source ~/.bashrc"