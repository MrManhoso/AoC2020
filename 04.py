import re

# [lower, upper]
def value_is_between(year, lower, upper):
    try:
        n = int(year)
        return n >= lower and n <= upper
    except:
        return False

# four digits; at least 1920 and at most 2002.
def valid_byr(value):
    return value_is_between(value, 1920, 2002)


# four digits; at least 2010 and at most 2020.
def valid_iyr(value):
    return value_is_between(value, 2010, 2020)


# four digits; at least 2020 and at most 2030.
def valid_eyr(value):
    return value_is_between(value, 2020, 2030)


# a number followed by either cm or in:
#     If cm, the number must be at least 150 and at most 193.
#     If in, the number must be at least 59 and at most 76.
def valid_hgt(value):
    if value.endswith('cm'): return value_is_between(value[:len(value)-2], 150, 193)
    if value.endswith('in'): return value_is_between(value[:len(value)-2], 59, 76)
    return False


# a # followed by exactly six characters 0-9 or a-f.
def valid_hcl(value):
    return len(value) == 7 and re.search('^#[a-f0-9]+', value)


eye_colors = {'amb', 'blu', 'brn', 'gry', 'grn', 'hzl', 'oth'}

# exactly one of: amb blu brn gry grn hzl oth.
def valid_ecl(value):
    return value in eye_colors


# a nine-digit number, including leading zeroes.
def valid_pid(value):
    return len(value) == 9 and re.search('[0-9]+', value)


def valid_field_part2(field):
    vals = field.split(':')
    if vals[0] == 'byr': return valid_byr(vals[1])
    if vals[0] == 'iyr': return valid_iyr(vals[1])
    if vals[0] == 'eyr': return valid_eyr(vals[1])
    if vals[0] == 'hgt': return valid_hgt(vals[1])
    if vals[0] == 'hcl': return valid_hcl(vals[1])
    if vals[0] == 'ecl': return valid_ecl(vals[1])
    if vals[0] == 'pid': return valid_pid(vals[1])
    return False;


def valid_field_part1(field):
    vals = field.split(':')
    return vals[0] != 'cid'


def count_required_keys(pairs, validator_func):
    return len([p for p in pairs if validator_func(p)])


def validate_line(line, count, required_keys_found):
    if(line == ""):
        if(required_keys_found >= 7): count += 1
        required_keys_found = 0
    else: required_keys_found += count_required_keys(line.split(), valid_field_part2) # change here to try out part1 filer or part2 filer
    return (count, required_keys_found)


def valid_passports(handle):
    count = 0
    required_keys_found = 0
    for line in handle:
        line = line.rstrip()
        (count, required_keys_found) = validate_line(line.rstrip(), count, required_keys_found)

    # trailing passport if file does not end with blank row
    if(required_keys_found == 7): count += 1

    return count

# TODO separate running of part1 and 2 better. Note that when looping over handle is single pass only
handle = open("passports.txt")
print(valid_passports(handle))