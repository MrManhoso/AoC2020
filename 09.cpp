#include <vector>
#include <string>
#include <fstream>
#include <iostream>
#include <array>
#include <algorithm>
#include <numeric>
#include <cassert>
#include <utility>
#include <iterator>

using std::vector;
using std::string;
using std::array;
using std::pair;

///////////////////////////////////////////////////
template<typename T>
void print(const T& t, bool endline = true)
{
    std::cout << t;
    if(endline) std::cout << '\n';
}

template<typename T>
string stringilize(const T& t){ return std::to_string(t); }
string stringilize(const string& str){ return str; }
template<typename T1, typename T2>
string stringilize(const std::pair<T1, T2>& p){
    return stringilize(p.first) + " : " + stringilize(p.second);
}

template<typename C>
std::string print_container(const C& c, const std::string& sep = " ")
{
    using val_type = typename C::value_type;
    return !c.empty() 
        ? std::accumulate(std::next(std::begin(c)), 
                    std::end(c), 
                    stringilize(*std::cbegin(c)), 
                    [&](std::string a, const val_type& b) {
                         return std::move(a) + sep + stringilize(b);
                     })
        : std::string();
}

/////////////////////////////////////////////////////////

// log(n)
void update_previous(long long insert, long long remove, vector<long long>& sorted_previous)
{
    auto f = std::lower_bound(std::cbegin(sorted_previous), std::cend(sorted_previous), remove);
    // if f == end there is something awfully wrong
    sorted_previous.erase(f);
    f = std::lower_bound(std::cbegin(sorted_previous), std::cend(sorted_previous), insert);
    sorted_previous.insert(f, insert);
    
}

// n log(n) currently
bool not_valid(long long val, const vector<long long>& sorted_previous)
{
    const auto end = std::cend(sorted_previous);
    for(auto it = std::cbegin(sorted_previous); it != end; ++it){
        // print(1);
        const auto next = std::next(it);
        if(next == end) break;
        // print(2);
        auto curr = *it;
        if(curr > val) break;
        // print(3);
        auto diff = val-curr;
        // we have to take next since we cannot use the same value twice
        const auto f = std::lower_bound(next, end, diff);
        if(f != end && *f == diff) return false;
        // print(4);
    }
    return true;
}

int find_first_invalid(const vector<long long>& input, int size)
{
    // maybe use list since we will do alot of insert/delete?
    vector<long long> sorted_previous;
    sorted_previous.assign(std::cbegin(input), std::cbegin(input) + size);
    std::sort(std::begin(sorted_previous), std::end(sorted_previous));
    int index = size;
    while(index < input.size()){
        auto val = input[index];
        if(not_valid(val, sorted_previous)) return index;
        update_previous(val, input[index-size], sorted_previous);
        ++index;
    }
    return -1;
}

vector<long long> read_input(const string& file)
{
    std::ifstream in(file, std::ios_base::in);
    string line;
    vector<long long> ret;
    while(std::getline(in, line)){
        ret.emplace_back(stoll(line));
    }
    return std::move(ret);
}

// asserts [s,e)
long long min_max_sum(int start, int end, const vector<long long>& input)
{
    auto s = std::begin(input);
    std::advance(s, start);
    auto e = std::begin(input);
    std::advance(e, end);
    auto mm = std::minmax_element(s, e);
    return *mm.first + *mm.second;
}

long long encryption_weakness(long long val, const vector<long long>& input)
{
    vector<long long> part_sum;
    std::partial_sum(std::begin(input), std::end(input), std::back_inserter(part_sum));
    int i = 0;
    auto f = std::lower_bound(std::cbegin(part_sum), std::cend(part_sum), val);
    auto greater_than = std::distance(std::cbegin(part_sum), f);
    if(part_sum[greater_than] == val) return min_max_sum(i, greater_than+1, input); //input[i] + input[greater_than];
    while(i < part_sum.size()-1){
        // todo kolla så inte i+1 == greater_than
        auto diff = part_sum[greater_than] - part_sum[i];
        if(diff == val) return min_max_sum(i+1, greater_than+1, input);
        if(diff < val) ++greater_than;
        else ++i;
        assert(i+1 < greater_than);
    }
    return -1;
}

int main()
{
    auto file("xmas.txt");
    auto input = read_input(file);
    // correct for part 1 is 22406676
    auto i = find_first_invalid(input, file == "xmas_test.txt" ? 5 : 25);
    std::cout << "Part 1: " << input[i] << '\n';
    std::cout << "Part 2: " << encryption_weakness(input[i], input) << '\n';
    return 0;
}