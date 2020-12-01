#include <fstream>
#include <string>
#include <iostream>
#include <vector>
#include <utility>
#include <cassert>
#include <algorithm>

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
}

std::pair<int, int> get_2020_nrs(std::vector<int>& expense_report)
{
    sort(std::begin(expense_report), std::end(expense_report));
    for(std::vector<int>::const_iterator it = std::begin(expense_report); it != std::end(expense_report); ++it){
        auto x = 2020 - (*it);
        if(x < 0) continue;
        if(std::binary_search(std::begin(expense_report), std::end(expense_report), x)) return std::make_pair(*it, x);
    }
    return std::make_pair(-1, -1);
}

int main()
{
    std::vector<int> expense_report;
    get_expense_report(std::back_inserter(expense_report));
    auto nrs = get_2020_nrs(expense_report);
    assert(nrs.first + nrs.second == 2020);
    std::cout << nrs.first << ' ' << nrs.second << '\n';
    std::cout << nrs.first * nrs.second << '\n';
    return 0;
}