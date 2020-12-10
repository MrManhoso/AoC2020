#include <fstream>
#include <string>
#include <iostream>
#include <vector>
#include <utility>
#include <cassert>
#include <algorithm>
#include <tuple>

template<typename I>
void get_expense_report(I i)
{
    std::ifstream in("expense_report.txt", std::ios_base::in);
    if(!in.is_open()){
        std::cout << "Could not open expense file." << '\n';
        return;
    }
    std::string line;
    while(std::getline(in, line)){
        i = stoi(line);
    }
    in.close();
}

// asserts sorted
std::pair<int, int> get_tot_as_2nrs(const std::vector<int>& expense_report, int tot)
{
    for(std::vector<int>::const_iterator it = std::begin(expense_report); it != std::end(expense_report); ++it){
        auto x = tot - (*it);
        if(x < 0) continue;
        if(std::binary_search(std::begin(expense_report), std::end(expense_report), x)) return std::make_pair(*it, x);
    }
    return std::make_pair(-1, -1);
}

// asserts sorted
std::tuple<int, int, int> get_tot_as_3nrs(const std::vector<int>& expense_report, int tot)
{
    for(std::vector<int>::const_iterator it = std::begin(expense_report); it != std::end(expense_report); ++it){
        auto sub_tot = tot - (*it);
        if(sub_tot < 0) continue;
        // TODO get partition of numbers smaller than sub_tot
        auto lb = std::lower_bound(std::begin(expense_report), std::end(expense_report), sub_tot);
        if(lb == std::begin(expense_report)) continue;
        auto p = get_tot_as_2nrs(std::vector<int>(std::begin(expense_report), lb), sub_tot);
        if(p.first != -1) return std::make_tuple(*it, p.first, p.second);
    }
    return std::make_tuple(-1, -1, -1);
}

int main()
{
    std::vector<int> expense_report;
    get_expense_report(std::back_inserter(expense_report));
    sort(std::begin(expense_report), std::end(expense_report));
    auto nrs2 = get_tot_as_2nrs(expense_report, 2020);
    assert(nrs2.first + nrs2.second == 2020);
    std::cout << nrs2.first << ' ' << nrs2.second << '\n';
    std::cout << "Two numbers: " << nrs2.first * nrs2.second << '\n';

    int first, second, third;
    std::tie(first, second, third) = get_tot_as_3nrs(expense_report, 2020);
    assert(first + second + third == 2020);
    std::cout << first << ' ' << second << ' ' << third << '\n';
    std::cout << "Three numbers: " << first * second * third << '\n';

    return 0;
}