# ex: nop +0
def execute(instruction, accumulator, index):
    parts = instruction.split()
    if(parts[0] == 'nop'): return (index + 1, accumulator)
    val = int(parts[1])
    if(parts[0] == 'acc'): return (index + 1, accumulator + val)
    if(parts[0] == 'jmp'): return (index + val, accumulator)
    raise Exception(f'Instruction {parts[0]} not supported')


def run(boot_seq):
    visited = set()
    index = 0
    accumulator = 0;
    nr_instructions = len(boot_seq)
    while(index < nr_instructions):
        if(index in visited): return (accumulator, False)
        instruction = boot_seq[index]
        visited.add(index)
        (index, accumulator) = execute(instruction, accumulator, index)
    return (accumulator, True)


def part1(boot_seq):
    res = run(boot_seq)
    print("First part:", res[0], "Terminated:", res[1])


def get_not_reachable(reachables):
    return [t[0] for t in dict(filter(lambda tup: len(tup[1]) == 0, reachables.items())).items()]


def create_reachables(boot_seq):
    reachables = {}
    [reachables.setdefault(x, []) for x in range(1, len(boot_seq))] 
    alternative_reach = {}
    for i in range(len(boot_seq)-1):
        parts = boot_seq[i].split()
        val = int(parts[1])
        if(parts[0] == 'nop'): 
            reachables[i + 1].append(i)
            alternative_reach[i + val] = alternative_reach.get(i + val, []) + [(i, f'jmp {str(val)}')]
        elif(parts[0] == 'jmp'):
            reachables[i + val].append(i)
            alternative_reach[i + 1] = alternative_reach.get(i + 1, []) + [(i, f'nop {str(val)}')]
        elif(parts[0] == 'acc'):
            reachables[i + 1].append(i)
    return (reachables, alternative_reach)


def try_alternative(alt, boot_seq):
    inst_at_n = boot_seq[alt[0]]
    boot_seq[alt[0]] = alt[1]
    res = run(boot_seq)
    boot_seq[alt[0]] = inst_at_n
    if(res[1] is True): 
        print("Part 2:", res[0])
        return True
    return False


def try_alternatives(alt_vals, boot_seq):
    for alt in alt_vals:
        if try_alternative(alt, boot_seq): return True
    return False


# correct result is 1532
# one nop is jmp or one jmp is nop
# something with a cyclic graph maybe?
def part2(boot_seq):
    (reachables, alternative_reach) = create_reachables(boot_seq)
    not_reachable = get_not_reachable(reachables)
    for n in not_reachable:
        alt_vals = alternative_reach.get(n, [])
        if(try_alternatives(alt_vals, boot_seq)): break
    

handle = open("boot.txt", mode='r')
boot_seq = [line.rstrip() for line in handle]
# part1(boot_seq)
part2(boot_seq)