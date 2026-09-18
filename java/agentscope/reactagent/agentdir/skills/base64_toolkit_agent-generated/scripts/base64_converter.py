#!/usr/bin/env python3
"""
Base64 Encoder and Decoder Script
Usage:
  echo 'Hello World' | python base64_converter.py --action encode
  echo 'SGVsbG8gV29ybGQ=' | python base64_converter.py --action decode
"""
import sys
import base64
import argparse

def main():
    parser = argparse.ArgumentParser(description="Base64 Encoder and Decoder")
    parser.add_argument(
        "--action",
        choices=["encode", "decode"],
        default="encode",
        help="Action to perform (default: encode)"
    )
    args = parser.parse_args()

    input_data = sys.stdin.read()
    if not input_data.strip():
        print("Error: No input provided. Please pipe data to stdin.", file=sys.stderr)
        sys.exit(1)

    try:
        if args.action == "encode":
            # Encode text to base64
            encoded = base64.b64encode(input_data.encode('utf-8')).decode('utf-8')
            print(encoded)
        else:
            # Decode base64 to text
            decoded = base64.b64decode(input_data.strip()).decode('utf-8')
            print(decoded)
    except Exception as e:
        print(f"Error during {args.action}: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()
