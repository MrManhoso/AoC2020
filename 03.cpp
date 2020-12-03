#include <fstream>
#include <string>
#include <iostream>
#include <vector>
#include <utility>
#include <cassert>
#include <algorithm>
#include <tuple>

using TreeMap = std::vector<std::string>;

template<typename T>
void print(const T& msg, bool endln = true)
{
    std::cout << msg;
    if(endln) std::cout << '\n';
}

TreeMap get_map()
{
    TreeMap vec;
    std::ifstream in("tree_map.txt", std::ios_base::in);
    if(!in.is_open()) print("Could not open map file.");
    else {
        std::string line;
        while(std::getline(in, line)) vec.emplace_back(line);
        in.close();
    }
    return std::move(vec);
}

int count_trees(const TreeMap& tree_map, int right, int down)
{
    auto line_len = static_cast<int>(tree_map[0].size());
    int pos = 0;
    int line = 0;
    return std::count_if(std::next(std::begin(tree_map)), std::end(tree_map), [=, &pos, &line](const std::string& str){
        ++line;
        if(line % down != 0) return false;
        pos += right;
        return str[pos%line_len] == '#';
    });
}

// Starting at the top-left corner of your map and following a slope of right 3 and down 1, how many trees would you encounter?
void part_one(const TreeMap& tree_map)
{
    auto count = count_trees(tree_map, 3, 1);
    print(count); 
}


// Right 1, down 1.
// Right 3, down 1. (This is the slope you already checked.)
// Right 5, down 1.
// Right 7, down 1.
// Right 1, down 2.
// What do you get if you multiply together the number of trees encountered on each of the listed slopes?
void part_two(const TreeMap& tree_map)
{
    long long count = count_trees(tree_map, 1, 1);
    count *= count_trees(tree_map, 3, 1);
    count *= count_trees(tree_map, 5, 1);
    count *= count_trees(tree_map, 7, 1);
    count *= count_trees(tree_map, 1, 2);
    print(count);
}

int main()
{
    const TreeMap tree_map = get_map();
    part_one(tree_map);
    part_two(tree_map);
    return 0;
}