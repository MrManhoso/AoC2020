#include <vector>
#include <string>
#include <fstream>
#include <iostream>
#include <array>
#include <algorithm>
#include <numeric>

using std::vector;
using std::string;
using std::array;

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

long long find_first_invalid(const vector<long long>& input, int size)
{
    // maybe use list since we will do alot of insert/delete?
    vector<long long> sorted_previous(std::cbegin(input), std::cbegin(input) + size);
    std::sort(std::begin(sorted_previous), std::end(sorted_previous));
    int index = size;
    while(index < input.size()){
        auto val = input[index];
        if(not_valid(val, sorted_previous)) return val;
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

int main()
{
    auto file("xmas.txt");
    auto input = read_input(file);
    std::cout << "Part 1: " << find_first_invalid(input, file == "xmas_test.txt" ? 5 : 25) << '\n';
    return 0;
}