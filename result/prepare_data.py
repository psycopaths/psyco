from glob import glob
from os import walk, makedirs, path
from os.path import sep as folder_separator
values = dict()
files_counter = dict()

def update_list(value, filename):
    with open(filename, "r") as input_file:
        for index, row in enumerate(input_file):
            index -= 1
            if not row.startswith("#") and not row.startswith('\n'):
                newvalue = int(row.split("\t")[1])
                if(index < len(value)):
                    value[index] = value[index] + newvalue
                else:
                    value.append(newvalue)
    return value
def handle_file(file, name):
    guardtimes = values.get(name, [])
    guard_files = files_counter.get(name, 0)
    guardtimes = update_list(guardtimes, file)
    values[name] = guardtimes
    guard_files += 1
    files_counter[name] = guard_files

def writeResult(values, count, filename):
    with open(filename, "w") as output_file:
        output_file.write("0\t0\n")
        for index, value in enumerate(values):
            output_file.write(str(index+1) + '\t' + str(value/count) +'\n')

def execute_for_example(example_under_preparation):
    folder = '.' + folder_separator + example_under_preparation + folder_separator
    files = walk(folder).next()[2]
    for file in files:
        complete_file_name = folder + file
        if file.startswith("guardTimes"):
            handle_file(complete_file_name, 'guard')
        if file.startswith("difference"):
            handle_file(complete_file_name, 'difference')
        if file.startswith("renaming"):
            handle_file(complete_file_name, 'renaming')
        if file.startswith("transition"):
            handle_file(complete_file_name, 'transition')
        # if file.startswith("profiler"):
            # handle_profiler_file(complete_file_name)
        # if file.startswith("searchResult"):
        #     handle_search_result(complete_file_name)

    filebase = '.' + folder_separator + 'plots' + folder_separator 
    filebase += example_under_preparation + folder_separator
    if not path.exists(filebase):
        makedirs(filebase)
    filename =  filebase + 'guard.csv'
    writeResult(values['guard'], files_counter['guard'], filename)

    filename = filebase + 'difference.csv'
    writeResult(values['difference'], files_counter['difference'], filename)

    filename = filebase + 'renaming.csv'
    writeResult(values['renaming'], files_counter['renaming'], filename)

    filename = filebase+ 'transition.csv'
    writeResult(values['transition'], files_counter['transition'], filename)

    with open(filebase + 'gnuplot.plot', 'w') as plot:
        plot.write("set terminal postscript eps size 6,3 enhanced color font 'Helvetica, 20'\n")
        plot.write("set xrange[0:" + str(len(values['transition'])) +"]\n")
        plot.write('set ylabel "difference time[ms] per iteration"\n')
        plot.write("set nokey\n")
        plot.write("set output '" +  example_under_preparation +"_diff.eps'\n")
        plot.write('plot "difference.csv" using 1:2 with lines\n')

        plot.write("set output '" +  example_under_preparation +"_guard.eps'\n")
        plot.write('plot "guard.csv" using 1:2 with lines\n')

        plot.write("set output '" +  example_under_preparation +"_renaming.eps'\n")
        plot.write('plot "renaming.csv" using 1:2 with lines\n')

        plot.write("set output '" +  example_under_preparation +"_transition.eps'\n")
        plot.write('plot "transition.csv" using 1:2 with lines\n')
    with open("excute_all_gnuplots.sh", "a") as master:
        master.write("pushd " + filebase.replace("\\", "/") +";\n")
        master.write("\tgnuplot ./gnuplot.plot" + ";\n")
        master.write("popd;\n")

examples=['cev_terminated', 'abp', 'cev', 'first_Example', 'gsoc_cev_state_machine', 'gsocCEV', 'io', 'math', 'net', 'security']
#examples=['net']
with open("excute_all_gnuplots.sh", "w") as master:
    master.write("#!/bin/bash\n")
for example in examples:
    values = dict()
    files_counter = dict()
    execute_for_example(example)