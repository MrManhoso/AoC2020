import functools as ft

def group_sum_any(answers):
    return len(ft.reduce(lambda a,b: a.union(set(b)), answers, set()))


def group_sum_all(answers):
    start = set(answers[0])
    return len(ft.reduce(lambda a,b: a.intersection(set(b)), answers[1:], start))


def sum_of_yes(grouped_answers, group_sum_func):
    return sum([group_sum_func(answers) for answers in grouped_answers])
    

def group_answers(answers, line):
    line = line.rstrip()
    if(len(line) == 0): answers.append([])
    else: answers[len(answers)-1].append(line)
    return answers


handle = open("yes.txt", mode='r')
grouped_answers = ft.reduce(group_answers, handle, [[handle.readline().rstrip()]])
print("Part 1:", sum_of_yes(grouped_answers, group_sum_any))
print("Part 2:", sum_of_yes(grouped_answers, group_sum_all))

handle.close()