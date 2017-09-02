import csv
import os
import sys
import re

def read_file(file_name):
    with open(file_name, 'rb') as csvfile:
        reader = csv.DictReader(csvfile)
        sum = 0
        line = 0
        lines_used = 0
        name = ''
        key_re = re.search('-(\d+)range', file_name)
        key_range = key_re.group(1)
        for row in reader:
            name = row['name']
            if line >= 5:
                sum += (int)(row['throughput'])
                lines_used += 1
            line += 1
        average = sum/lines_used
        return [name, key_range, average]


def parse_dir(output_file, dir):
    results = {}
    for filename in os.listdir(dir):
        if filename.endswith(".csv") and not filename == 'results.csv':
            [name, key_range, avg] = read_file(dir+ '/' + str(filename))
            if key_range not in results:
                results[key_range] = dict()
            results[key_range][name] = avg
    with open(output_file, 'w') as csvfile:
        fieldnames = ['range', 'BLTree', 'BST', 'AVL', 'Snap', 'SkipList', 'SyncTMAP']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        for name in results:
            results[name]['range'] = name
            writer.writerow(results[name])


parse_dir(sys.argv[1] + '/results.csv', sys.argv[1])
