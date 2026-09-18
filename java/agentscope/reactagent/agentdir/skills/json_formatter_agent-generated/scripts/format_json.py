#!/usr/bin/env python3
"""
JSON Formatter and Validator Script
Usage:
  echo '{"key": "value"}' | python format_json.py --action pretty
  echo '{"key": "value"}' | python format_json.py --action minify
  echo '{"key": "value"}' | python format_json.py --action validate
"""
import sys
import json
import argparse

def main():
    parser = argparse.ArgumentParser(description="JSON Formatter and Validator")
    parser.add_argument(
        "--action", 
        choices=["pretty", "minify", "validate"], 
        default="pretty", 
        help="Action to perform (default: pretty)"
    )
    parser.add_argument(
        "--indent", 
        type=int, 
        default=2, 
        help="Indentation level for pretty print (default: 2)"
    )
    args = parser.parse_args()

    input_data = sys.stdin.read()
    if not input_data.strip():
        print("Error: No input provided. Please pipe JSON data to stdin.", file=sys.stderr)
        sys.exit(1)

    try:
        data = json.loads(input_data)
        if args.action == "validate":
            print("Valid JSON structure.")
        elif args.action == "minify":
            print(json.dumps(data, separators=(',', ':'), ensure_ascii=False))
        else:
            print(json.dumps(data, indent=args.indent, ensure_ascii=False, sort_keys=False))
    except json.JSONDecodeError as e:
        print(f"JSON Syntax Error: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()
