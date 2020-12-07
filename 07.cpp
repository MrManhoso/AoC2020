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
using bag_count = pair<string, int>;   
using bags_count = vector<bag_count>;
// key: color that is contained by colors in value
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
    // print("split 1");
    
    std::stringstream ss(str);
    std::string token;
    vector<string> ret;
    while (std::getline(ss, token, delim)) {
        // print(token);

        if(token.size() == 0) continue;
        auto found = token.find_first_not_of(" ");
        // print(found);
        if(found != string::npos) token = token.substr(found);
        found = token.find(" bag");
        if(found != string::npos) token = token.substr(0, found);
        ret.emplace_back(token);
        
        // print("split while last line");
    }
    
    // print("split 2");

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

// not really a bags_count as described above, just a vec of colors and how many of it 
bags_count get_contained_colors(const string& contained_colors)
{
    // print(contained_colors);

    if(contained_colors == "no other bags.") return bags_count{{"", 0}};

    // print("get_contained_colors 2");

    auto nrs_and_cols(split(contained_colors, ','));

    // print(print_container(nrs_and_cols));

    bags_count ret; 
    for(const auto nr_and_col : nrs_and_cols){
        auto found = nr_and_col.find_first_of(' ');
        auto nr = stoi(nr_and_col.substr(0, found));
        auto color = nr_and_col.substr(found+1);
        ret.emplace_back(std::make_pair(color, nr));
    }

    // print(print_container(ret));
    
    return std::move(ret);
}

contained_bags map_rule(contained_bags& curr, const string& rule)
{
    // print("map_rule 1");

    static const string to_find(" bags contain ");
    auto found = rule.find(to_find);
    if(found == string::npos) return curr;
    
    // print("map_rule 2");

    auto containing_color(rule.substr(0, found));
    found += to_find.size();
    auto contained_colors(get_contained_colors(rule.substr(found)));
    
    // print(print_container(contained_colors));

    auto it = std::remove_if(std::begin(contained_colors), std::end(contained_colors), [](const bag_count& bc){ return bc.second == 0; });
    contained_colors.erase(it, std::end(contained_colors));
    
    // print("----------------------------------");
    // print(print_container(contained_colors));
    
    for(const auto& cc : contained_colors){
        // auto v = curr[cc.first];
        curr[cc.first].push_back(std::make_pair(containing_color, cc.second));
        // print(print_container(curr[cc.first]));
    }

    // print("map_rule 3");
    
    return curr;
}

contained_bags map_rules_to_bagcolors(const vector<string>& rules)
{
    // first: color that can be contained by second
    // second: vector of colors that should contain first + how many of first
    map<string, vector<string>> ret;

    // print("map_rules_to_bagcolors 1");
    
    return std::accumulate(std::cbegin(rules), std::cend(rules), contained_bags(), [](contained_bags curr, const std::string& rule){
        return map_rule(curr, rule);
    });
}

set<string> bagcolors_for(const string& bag_to_contain, const contained_bags& inverted_rules)
{
    // print("bagcolors_for 1");
    // TODO traveser map starting with bag_to_contain, could prob be done recursively
    set<string> ret;
    auto it_cols = inverted_rules.find(bag_to_contain);
    if(it_cols == std::cend(inverted_rules)) return ret;

    // print("bagcolors_for 2");
    
    for(const auto col : (*it_cols).second){
        auto s = bagcolors_for(col.first, inverted_rules);

        // print(print_container(s));

        std::set_union(std::cbegin(ret), std::cend(ret), std::cbegin(s), std::cend(s), std::inserter(ret, std::end(ret)));
        ret.insert(col.first);
    }
    
    // print("bagcolors_for 3");

    return ret;
}

int nr_bagcolors_for(const string& bag_to_contain, const contained_bags& inverted_rules)
{
    // print_map(inverted_rules);
    return bagcolors_for(bag_to_contain, inverted_rules).size();
}

int main()
{
    auto rules(get_rules("bagrules.txt"));
    std::cout << "Part 1: " << nr_bagcolors_for("shiny gold", map_rules_to_bagcolors(rules)) << '\n';
    return 0;
}