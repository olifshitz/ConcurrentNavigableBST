import csv
import os
import sys

def read_file(file_name):
    with open(file_name, 'rb') as csvfile:
        reader = csv.DictReader(csvfile)
        sum = 0
        line = 0
        lines_used = 0
        name = ''
        threads = ''
        for row in reader:
            name = row['name']
            threads = row['nthreads']
            if line >= 5:
                sum += (int)(row['throughput'])
                lines_used += 1
            line += 1
        average = sum/lines_used
        return [name, threads, average]


def parse_dir(output_file, dir):
    results = {}
    for filename in os.listdir(dir):
        if filename.endswith(".csv") and not filename == 'results.csv':
            [name, threads, avg] = read_file(dir+ '/' + str(filename))
            if threads not in results:
                results[threads] = dict()
            results[threads][name] = avg
    with open(output_file, 'w') as csvfile:
        fieldnames = ['threads', 'BLTree', 'ConcurrentHMAP', 'BST', 'AVL', 'Snap', 'SkipList']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        for name in results:
            results[name]['threads'] = name
            writer.writerow(results[name])


parse_dir(sys.argv[1] + '/results.csv', sys.argv[1])

