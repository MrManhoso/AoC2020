#include <vector>
#include <string>
#include <fstream>
#include <iostream>
#include <utility>
#include <numeric>
#include <cassert>
#include <algorithm>

// if "lower" split and return lower half, else return upper half
std::pair<int,int> split_range(const std::pair<int,int>& range, bool lower)
{
    auto m = (range.second - range.first)/2 + range.first;
    
    // std::cout << std::boolalpha << lower << " " << range.first << " " << range.second << " " << m << '\n';

    if(lower) return std::make_pair(range.first, m);
    return std::make_pair(m, range.second);
}

int get_val(const std::string& str, int size, char lower)
{
    return std::accumulate(std::begin(str), std::end(str), std::make_pair(0, size), [=](const std::pair<int,int>& curr, char c){ return split_range(curr, c == lower); }).first;
}

int get_col(const std::string& seat){ return get_val(seat.substr(7, 3), 8, 'L'); }
int get_row(const std::string& seat){ return get_val(seat.substr(0, 7), 128, 'F'); }

// first = row, second = col
std::pair<int, int> get_seat_pos(const std::string& seat)
{
    int row = get_row(seat);
    int col = get_col(seat);
    return std::make_pair(row, col);
}

int get_seat_id(const std::pair<int, int>& seat_pos)
{
    return seat_pos.first * 8 + seat_pos.second;
}
int get_seat_id(const std::string& seat){ return get_seat_id(get_seat_pos(seat)); }

std::vector<std::string> get_seats()
{
    std::ifstream in("seats.txt", std::ios_base::in);
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

template<typename T>
void validate(const T& t1, const T& t2)
{
    if(t1 != t2) std::cout << "Validation failed: " << t1 << " does not equal " << t2 << '\n';
    else std::cout << "OK" << '\n';
}

int get_highest_id(const std::vector<std::string>& seats)
{
    return get_seat_id(seats[seats.size()-1]);
}

// Precondition not in rows:
// First row = FFFF...
// Last row = BBBB...
// Though the one preceding might be in FFFFBBBRRR (last in FFFF)
// or the one following might be in BBBBFFFLLL (first in BBBB)
// Seats is sorted lower to higher
// the seats with IDs +1 and -1 from yours will be in your list.
int get_my_id(const std::vector<std::string>& seats)
{
    auto lowest_seat(get_seat_id("FFFFBBBRRR"));
    auto s = std::lower_bound(std::cbegin(seats), std::cend(seats), lowest_seat, [](const std::string& seat, int value){
        return get_seat_id(seat) < value;
    });
    if(s == std::cend(seats)) {
        std::cout << "Could not find start of my seat ID search." << '\n';
        return -1;
    }

    auto highest_seat(get_seat_id("BBBBFFFLLL"));
    const auto e = std::upper_bound(std::cbegin(seats), std::cend(seats), highest_seat, [](int value, const std::string& seat){
        return value < get_seat_id(seat);
    });

    if(e == std::cend(seats)){
        std::cout << "Could not find end of my seat ID search." << '\n';
        return -1;
    }

    auto prev = -1;
    for(std::next(s); s < e; ++s){
        prev = get_seat_id(*std::prev(s));
        auto curr = get_seat_id(*s);
        if(curr - prev == 2) return prev + 1;
        prev = curr;
    }
    return -1;
}

void test(const std::string& seat, int exp_row, int exp_col, int exp_id)
{
    auto seat_pos = get_seat_pos(seat);
    validate(seat_pos.first, exp_row);
    validate(seat_pos.second, exp_col);
    // assert(seat_pos.first == exp_row);
    // assert(seat_pos.second == exp_col);
    auto seat_id = get_seat_id(seat_pos);
    validate(seat_id, exp_id);
    // assert(seat_id == exp_id);
}

void tests()
{
    test("BFFFBBFRRR", 70, 7, 567);
    test("FFFBBBFRRR", 14, 7, 119);
    test("BBFFBBFRLL", 102, 4, 820);

    std::vector<std::string> seats{"BFFFBBFRRR", "FFFBBBFRRR", "BBFFBBFRLL"};
    validate(get_highest_id(seats), 820);
}

void sort_seats(std::vector<std::string>& seats)
{
    std::sort(std::begin(seats), std::end(seats), [](const std::string& s1, const std::string& s2){
        int i = 0;
        while(i < 7){
            if(s1[i] != s2[i]) return s1[i] == 'F';
            ++i;
        }
        while(i < 10){
            if(s1[i] != s2[i]) return s1[i] == 'L';
            ++i;
        }
        return false;
    });
}

int main()
{
    // tests();
    auto seats(get_seats());
    sort_seats(seats);
    std::cout << "Part 1. Highest Seat ID: " << get_highest_id(seats) << '\n';
    std::cout << "Part 2. My Seat ID: " << get_my_id(seats) << '\n';
    
    return 0;
}