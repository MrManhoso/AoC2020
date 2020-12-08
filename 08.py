# ex: nop +0
def execute(instruction, accumulator):
    parts = instruction.split()
    if(parts[0] == 'nop'): return (1, accumulator)
    val = int(parts[1])
    if(parts[0] == 'acc'): return (1, accumulator + val)
    if(parts[0] == 'jmp'): return (val, accumulator)
    raise Exception(f'Instruction {parts[0]} not supported')


def run_until_repeat(boot_seq):
    visited = set()
    index = 0
    accumulator = 0;
    while(True):
        if(index in visited): return accumulator
        instruction = boot_seq[index]
        (jmp, accumulator) = execute(instruction, accumulator)
        visited.add(index)
        index += jmp



handle = open("boot.txt", mode='r')
boot_seq = [line.rstrip() for line in handle]
print("First part:", run_until_repeat(boot_seq))