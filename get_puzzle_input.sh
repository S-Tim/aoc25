#!/bin/bash

# Determine for which day the puzzle template should be created
if [[ -z ${1+x} ]]; then
  day=$(date '+%-d')
  echo "No day specified, using today's day of month ($day) as a default..."
else
  day=$1
fi

day_formatted=$(printf '%2.2d' "$day")
output_dir="src/day$day_formatted"
input_file="$output_dir/day$day_formatted.txt"
test_input_file="$output_dir/day${day_formatted}_test.txt"
template_file="src/day00/Day00.kt"
output_file="$output_dir/Day$day_formatted.kt"
input_url="https://adventofcode.com/2025/day/$day/input"
# The download is secured by a sessions cookie which is valid for about a month
session_cookie="session_cookie.txt"

if [[ ! -f "$session_cookie" ]]; then
  echo "Sessions cookie not found..."
  echo "Place a file named $session_cookie in this directory"
  echo "The content should have the form session=..."
  exit 1
else
  session_cookie=$(<$session_cookie)
fi

if [[ ! -d "$output_dir" ]]; then
  echo "Creating directory..."
  mkdir "$output_dir"
else
  echo "Puzzle directory already exists..."
fi

if [[ ! -f "$input_file" ]]; then
  echo "Downloading puzzle input..."
  wget --header "Cookie: $session_cookie" "$input_url" -O "$input_file"
else
  echo "Puzzle input file already exists..."
fi

if [[ ! -f "$test_input_file" ]]; then
  echo "Creating template file for test input..."
  touch "$test_input_file"
else
  echo "Puzzle test input file already exists..."
fi

if [[ ! -f "$output_dir/Day$day_formatted.kt" ]]; then
  echo "Creating template file for solution..."
  cp "$template_file" "$output_file"
  # Replace Day01 and day01 in the template file with the correct day
  sed -i '' "s/Day00/Day$day_formatted/g" "$output_file"
  sed -i '' "s/day00/day$day_formatted/g" "$output_file"
else
  echo "Template file already exists..."
fi
