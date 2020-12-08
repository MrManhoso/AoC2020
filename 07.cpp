#include <vector>
#include <string>
#include <fstream>
#include <iostream>
#include <map>
#include <numeric>
#include <utility>
#include <sstream>
#include <iterator>
#include <set>
#include <algorithm>

using std::vector;
using std::string;
using std::map;
using std::pair;
using std::set;

// how many (second) of some other bag color this bag color (first) contains
// OR how many (second) of bag color (first) that is contained
using bag_count = pair<string, int>;   
using bags_count = vector<bag_count>;
// key: bag/color that is contained by bags/colors in value OR bag/color that contains the bags/colors in value
// value: bags_count
using contained_bags = map<string, bags_count>;

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

void print_map(const contained_bags& inverted_rules)
{
    for(auto i : inverted_rules){
        print(i.first + ": ");
        print_container(i.second);
    }
}

vector<string> split(const std::string& str, char delim = ' ')
{ 
    std::stringstream ss(str);
    std::string token;
    vector<string> ret;
    while (std::getline(ss, token, delim)) {
        if(token.size() == 0) continue;
        auto found = token.find_first_not_of(" ");
        if(found != string::npos) token = token.substr(found);
        found = token.find(" bag");
        if(found != string::npos) token = token.substr(0, found);
        ret.emplace_back(token);
    }
    return std::move(ret);
}

vector<string> get_rules(const string& input)
{
    std::ifstream in(input, std::ios_base::in);
    if(!in.is_open()){
        std::cout << "Could not open seats file" << '\n';
    }
    std::string line;
    std::vector<std::string> vec;
    while(std::getline(in, line)){
        vec.emplace_back(line);
    }
    in.close();
    return std::move(vec);
}

bags_count get_contained_colors(const string& contained_colors)
{
    if(contained_colors == "no other bags.") return bags_count();
    auto nrs_and_cols(split(contained_colors, ','));
    bags_count ret; 
    for(const auto nr_and_col : nrs_and_cols){
        auto found = nr_and_col.find_first_of(' ');
        auto nr = stoi(nr_and_col.substr(0, found));
        auto color = nr_and_col.substr(found+1);
        ret.emplace_back(std::make_pair(color, nr));
    }
    return std::move(ret);
}

pair<string, size_t> get_containing_color(const string& rule)
{
    static const string to_find(" bags contain ");
    auto found = rule.find(to_find);
    if(found == string::npos) return std::make_pair(string(), found);
    auto containing_color(rule.substr(0, found));
    found += to_find.size();
    return std::make_pair(containing_color, found);
}

void invert_rule_and_map(const string& containing_color, const bags_count& contained_colors, contained_bags& mapped)
{
    for(const auto& cc : contained_colors){
        mapped[cc.first].push_back(std::make_pair(containing_color, cc.second));
    }   
}

void map_containing_to_contained(const string& containing_color, const bags_count& contained_colors, contained_bags& mapped) 
{ 
    mapped[containing_color] = contained_colors; 
}

template<typename F>
contained_bags map_rule(contained_bags& curr, const string& rule, F mapper)
{
    auto cont_col = get_containing_color(rule);
    if(cont_col.first == "") return std::move(curr);
    auto contained_colors(get_contained_colors(rule.substr(cont_col.second)));
    // auto it = std::remove_if(std::begin(contained_colors), std::end(contained_colors), [](const bag_count& bc){ return bc.second == 0; });
    // contained_colors.erase(it, std::end(contained_colors));
    mapper(cont_col.first, contained_colors, curr);   
    return std::move(curr);
}

// first: color that can be contained by second
// second: vector of colors that should contain first + how many of first
template<typename F>
contained_bags map_rules(const vector<string>& rules, F mapper)
{
    return std::accumulate(std::cbegin(rules), std::cend(rules), contained_bags(), [=](contained_bags curr, const std::string& rule){
        return map_rule(curr, rule, mapper);
    });
}

set<string> bagcolors_containing_bag(const string& bag_to_contain, const contained_bags& inverted_rules)
{
    set<string> ret;
    auto it_cols = inverted_rules.find(bag_to_contain);
    if(it_cols == std::cend(inverted_rules)) return ret;
    for(const auto col : (*it_cols).second){
        auto s = bagcolors_containing_bag(col.first, inverted_rules);
        std::set_union(std::cbegin(ret), std::cend(ret), std::cbegin(s), std::cend(s), std::inserter(ret, std::end(ret)));
        ret.insert(col.first);
    }
    return ret;
}

int nr_bagcolors_for(const string& bag_to_contain, const contained_bags& inverted_rules)
{
    return bagcolors_containing_bag(bag_to_contain, inverted_rules).size();
}

// includes given color as one
long long sum_nr_bags(const string& color, const contained_bags& mapped_rules, bool include_current)
{
    auto it = mapped_rules.find(color);
    if(it == std::cend(mapped_rules)) return 0;
    auto mapped_rule = *it;

    return std::accumulate(std::cbegin(mapped_rule.second), std::cend(mapped_rule.second), include_current ? 1 : 0, [&](int curr, const pair<string,int>& c){
        return curr + c.second * sum_nr_bags(c.first, mapped_rules, true);
    });
}

// excludes given start color
long long nr_bags_inside_bag(const string& color, const contained_bags& mapped_rules)
{
    return sum_nr_bags(color, mapped_rules, false);
}

int main()
{
    auto rules(get_rules("bagrules.txt"));
    // expects 246 for "production" run
    // std::cout << "Part 1: " << nr_bagcolors_for("shiny gold", map_rules(rules, invert_rule_and_map)) << '\n';
    std::cout << "Part 2: " << nr_bags_inside_bag("shiny gold", map_rules(rules, map_containing_to_contained)) << '\n';
    return 0;
}