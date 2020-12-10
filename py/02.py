# import urllib.parse, urllib.request, ssl

# url = "http://adventofcode.com/2020/day/2/input"

# ctx = ssl.create_default_context()
# ctx.check_hostname = False
# ctx.verify_mode = ssl.CERT_NONE

# i get bad request 400, https related?
# data = urllib.request.urlopen(url, context = ctx).read()
# print(data)

import re


# gets policy as a tuple where
# first is min nr, second max nr, third is the character
def parse_policy(policy):
    s1 = policy.split()
    s2 = s1[0].split('-')
    return (int(s2[0]), int(s2[1]), s1[1])


def print_policy(p):
    print(f'Policy: {p[0]}, {p[1]}, {p[2]}')


# How many passwords are valid according to their policies?
# first part is policy as string, second is the password
def first_pussel(policy, word):
    count = word.count(policy[2])#count_char(policy[2], parts[1])
    return count >= policy[0] and count <= policy[1]


# Exactly one of these positions must contain the given letter.
# first part is policy as string, second is the password
def second_pussel(policy, word):
    if len(word) < policy[0]:
        return False
    first = word[policy[0]-1] == policy[2]
    second = False if len(word) < policy[1] else word[policy[1]-1] == policy[2]


    print_policy(policy)
    print(word, first, second)


    return not second if first is True else second  
    

def parse_lines(handle, f):
    found = []
    for line in handle:
        parts = line.rstrip().split(': ')
        policy = parse_policy(parts[0])
        if f(policy, parts[1]):
            found.append(parts[1])
            # print_policy(policy)
            # print(count, parts[1])
    print(len(found))

handle = open("passwords.txt")
# parse_lines(handle, first_pussel)
parse_lines(handle, second_pussel)
