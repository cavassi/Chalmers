
# Lab: Text indexing

In this lab, you will implement a very fast search engine for large text files. It has the following purposes:

* to teach sorting algorithms, both standard and tailor-made ones,
* how to tailor-make algorithms to special circumstances,
* more binary search training, and
* to experiment with different complexity classes.


## About the labs

* This lab is part of the examination of the course. Therefore, you must not copy code from or show code to other students. You are welcome to discuss general ideas with one another, but anything you do must be **your own work**.

* Further info on Canvas, see e.g. the following pages:
  - "General information"
  - "The lab system"
  - "Running the labs"

* You can solve this lab in either Java or Python, and it's totally up to you.
  - Note that since Python is an interpreted language, it's much slower than Java – about 20–40 times slower on the tasks in this lab. So if you want something that's blazingly fast you should choose Java. 
  - A faster alternative to standard Python is to use [PyPy](https://www.pypy.org) instead. It is 5–8 times faster than the standard Python interpreter.
  - You grade will not be affected by your choice of language! But if you choose Java you can experiment with larger texts than if you choose Python.

## Background

If we want to search for a string in a text file, we usually iterate through the file from the start until we find an occurrence of the string. This works fine for small-to-medium sized texts, but when the files contain millions of words this simple idea becomes too slow.

To solve this, we can calculate a *search index* in advance. Much like an index in a book, a search index is a data structure that allows us to quickly find all the places where a given string appears.

Search indexes are used in many applications – for example, database engines use them to be able to search quickly.

In this lab, you will build a search index using a data structure called a *suffix array*. Suffix arrays build on ideas from *sorting algorithms* and *binary search* to search efficiently in large texts.

Later in the course, you will learn about two more data structures, called *hash tables* and *search trees*, which can be used to implement a search index that can be updated quickly.

The lab has six parts (but some of them are rather short):

* In part 1, you will test a naive linear search algorithm and see that it is really slow.
* Part 2 tries to explain the theory behind suffix arrays.
* In part 3, you will implement versions of insertion sort and quicksort. You will use these implementations to build search indexes for large text files.
* In part 4, you will use the search index to find strings in the text file quickly.
* In part 5, you will implement multi-key quicksort, a specialized sorting algorithm for strings.
* In part 6, you will make an empirical complexity analysis of your implementations.

By the end, you will be able to search through large texts in a millisecond! (Building the index will take some time, but the search will be almost instantaneous.)

## Getting started

The lab directory contains several Java/Python files.

- **SuffixArray.java/suffix_array.py**: This is the main class for suffix arrays. It is explained in more detail below.

Files for building the suffix arrays:

- **InsertionSort.java/insertion_sort.py**: Insertion sort (*to be completed in task 3a*).
- **Quicksort.java/quicksort.py**: Quicksort (*to be completed in task 3b*).
- **MultikeyQuicksort.java/multikey_quicksort.py**: Multi-key quicksort (*to be completed in task 5*).
- **BuiltinSort.java/builtin_sort.py**: This uses Java's/Python's builtin sorting algorithm.
- **SuffixSorter.java/suffix_sorter.py**: An abstract class that the algorithms above implement.
- **PivotSelector.java/pivot_selector.py**: Several different pivot selection algorithms, used by quicksort.
- **BuildIndex.java/build_index.py**: Command-line program that you invoke to build a suffix array and save it to disk.

Files for searching in texts:

- **BinarySearch.java/binary_search.py**: Binary search in a suffix array (*to be completed in task 4*).
- **SearchIndex.java/search_index.py**: Command-line program with which you can search in texts.

And some general utilities:

- **ProgressBar.java/progress_bar.py**: A command-line progress bar, heavily inspired by the [tqdm](https://tqdm.github.io) library.
- **CommandParser.java/command_parser.py**: A command-line argument parser, heavily inspired by Python's builtin [argparse](https://docs.python.org/3/library/argparse.html) module.
- **Stopwatch.java/stopwatch.py**: A very simple class for measuring runtime.

And non-source code files:

- **answers.txt**: Here, you will write down answers to questions in this lab.
- **texts**: A directory containing several differently-sized text files for experimentation.


## Part 1: Testing naive text search

In this part you will experiment with a very stupid and slow baseline for searching for strings in texts.

It is already implemented in the method `linearSearch` in the `SuffixArray` class – take some time to read and understand it. The Python code is very simple, it justs loops through each position in the text and compares with the value you are searching for:

<table><tr><th>Python</th></tr><tr><td>

```python
def linearSearch(self, value: str) -> Iterable[int]:
    for start in range(self.size() - len(value)):
        end = start + len(value)
        if value == self.text[start : end]:
            yield start
```

</td></tr></table>


The `yield` keyword tells us that this is a *generator* function, which means that it behaves like a stream that produces results *on demand*. So if we only want one single result, it stops after the first match. But if we want another one it continues automatically to find the second match.

### Java: using an Iterable

Java doesn't have generator functions, so we have to implement the same function as an `Iterator` class instead. This is a bit more clumsy than the Python version, but it is instructive to learn how this is done. In fact, if you look at how the Python generator function is compiled, it behaves similarly to the Java version: 

<table><tr><th>Java</th></tr><tr><td>

```java
public Iterable<Integer> linearSearch(String value) {
    return new Iterable<>() {
        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<>() {
                int start = 0;
                int end = value.length();
                @Override
                public boolean hasNext() {
                    while (true) {
                        if (end >= size()) return false;
                        if (value.equals(text.substring(start, end))) return true;
                        start++; end++;
                    }
                }
                @Override
                public Integer next() {
                    if (hasNext()) {
                        end++; return start++;
                    }
                    throw new NoSuchElementException();
                }
            };
        }
    };
}
```

</td></tr></table>

### How to search in a text

You can already now use the program to search in text files, but only using linear search. You can simply run the program **SearchIndex.java/search_index.py** without arguments and answer the questions like this:

<pre>
$ java SearchIndex        (or: python search_index.py)
(...)
Enter values:
 * text file (utf-8 encoded): <b>../texts/bnc-larger.txt.gz</b>
 * use linear search (much slower than binary search)? (yes/+/true for true) (no/-/ENTER for false): <b>yes</b>
 * number of matches to show (default: 10 matches) (ENTER for 10): 
 * context to show to the left and right (default: 40 characters) (ENTER for 40): 
 * trim each search result to the matching line? (yes/+/true for true) (no/-/ENTER for false): 
Reading 26836050 chars '../texts/bnc-larger.txt.gz' took 0.20 seconds.

Search key (ENTER to quit): 
</pre>

Note that you have to answer the first two questions. The second one about linear search you have to answer "yes", because the other kind of search isn't implemented yet.

A more compact way of running the program is to provide the arguments directly on the command line:

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```
$ java SearchIndex --textfile ../texts/bnc-larger.txt.gz --linear-search
Reading 26836050 chars from '../texts/bnc-larger.txt.gz' took 0.30 seconds.
Search key (ENTER to quit): 
```

</td><td>

```
$ python search_index.py --textfile ../texts/bnc-larger.txt.gz --linear-search
Reading 26836050 chars from '../texts/bnc-larger.txt.gz' took 0.20 seconds.
Search key (ENTER to quit): 
```

</td></tr></table>



### Task for part 1: Perform some linear searches

Search for the following strings in the largest text file you have (such as **bnc-larger.txt.gz**): "and", "18th-century", and "this-does-not-exist". For each search, write down how many matches you get and how long time each query takes.

Write down your answers in **answers.txt**.


## Part 2: Suffix arrays (background information)

As you hopefully noted, linear search becomes quite slow when we search through large amounts of text. But there are much faster ways to search in text, and in this lab you will experiment with a very nice data structure called a *suffix array* that can be used as a search index for this problem.

In this section, imagine that we want to build a search index for the text "ABRACADABRA". A *suffix* is a substring of the text that starts at some position and goes all the way to the end of the text. For example, "ADABRA" is a suffix of the text above.

Conceptually, a suffix array consists of *all suffixes* of the text, sorted alphabetically. Here are the suffixes of our example text, together with the position (in characters) where each one starts, written as an array of pairs of the form `(position, suffix)`:

```
[ ( 0, "ABRACADABRA"),
  ( 1, "BRACADABRA"),
  ( 2, "RACADABRA"),
  ( 3, "ACADABRA"),
  ( 4, "CADABRA"),
  ( 5, "ADABRA"),
  ( 6, "DABRA"),
  ( 7, "ABRA"),
  ( 8, "BRA"),
  ( 9, "RA"),
  (10, "A"),
  (11, "") ]
```

The suffix array for the text is conceptually just this list of suffixes sorted alphabetically:

```
[ (11, ""),
  (10, "A"),
  ( 7, "ABRA"),
  ( 0, "ABRACADABRA"),
  ( 3, "ACADABRA"),
  ( 5, "ADABRA"),
  ( 8, "BRA"),
  ( 1, "BRACADABRA"),
  ( 4, "CADABRA"),
  ( 6, "DABRA"),
  ( 9, "RA"),
  ( 2, "RACADABRA") ]
```

Now, how can we find a specific string in the text? It turns out that *we can use binary search on the suffix array*! For example, suppose we want to find all occurrences of "BRA". Can you see how to do this?

Here is the idea:

* There are two occurrences of "BRA" ("A«**BRA**»CADABRA" and "ABRACADA«**BRA**»"). Put another way, there are two suffixes that *start with* "BRA" (positions 1 and 8).
* In alphabetical order, these suffixes must all be ≥ "BRA" and < "BRB". So they must come together in the suffix array (as you can see above), in one "block": `(8,"BRA")` followed by `(1,"BRACADABRA")`.
* In fact, the suffix array remains sorted when we restrict each suffix to just the first three characters (same length as "BRA").
* We can use binary search for "BRA" in this restricted suffix array! The first occurrence will be the start of the "block" and the last occurrence will be the end of the "block". Everything in between is a match.

Make sure you understand this before going on!

### We don't need to store the suffixes

If the text consists of N characters, the suffix array will consist of N strings, ranging from the whole text to a single character at the end. If we would every single suffix in this array, it would use up an enormous amount of memory (quadratic in N) – but we don't have to store it like that. Instead the suffix array is an array of integers, which are the positions in the text. This array is then sorted – not numerically by position, but alphabetically by the substring starting at that position. So, for the text above, we start with the array of positions «0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11» , and sort it to get «12, 10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2».

The class `SuffixArray` (in **SuffixArray.java/suffix_array.py**) contains two instance variables, `text` which is the text itself, and `index` which is the array of suffix positions:

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```java
public class SuffixArray {
    String text;
    int[] index;
    ...
}
```

</td><td>

```python
class SuffixArray:
    text: str
    index: List[int]
    ...

```

</td></tr></table>


### Comparing suffixes

To sort the suffix array we have to be able to compare the suffixes. For this purpose we provide the method `compareSuffixes`, which takes as input two integers, representing the two suffixes in the text. It returns -1 if the first suffix is smaller than the second, and +1 if it is larger. If they are equal the method returns 0.

(Exercise for you: when are two suffixes equal?)

One very common mistake is to try to compare the substrings using Java's/Python's substring methods:

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```java
// Don't do this!
public int compareSuffixes(int suffix1, int suffix2) {
    String s1 = this.text.substring(suffix1);
    String s2 = this.text.substring(suffix2);
    return s1.compareTo(s2);
}
```

</td><td>

```python
# And don't do this!
def compareSuffixes(self, suffix1: int, suffix2: int) -> int:
    s1 = self.text[suffix1:]
    s2 = self.text[suffix2:]
    return 0 if s1 == s2 else -1 if s1 < s2 else +1

```

</td></tr></table>

The problem with this approach is that taking the substring creates a brand new string. And if the text consists of a million characters, this will take a lot of time. So don't try this at home!

Instead we implement suffix comparison by iterating through the characters in the two substrings in parallel. When two characters are unequal we know that one suffix is smaller than the other:

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```java
public int compareSuffixes(int suffix1, int suffix2) {
    if (suffix1 == suffix2) {
        return 0;
    }
    int end = text.length();
    while (suffix1 < end && suffix2 < end) {
        char ch1 = text.charAt(suffix1);
        char ch2 = text.charAt(suffix2);
        if (ch1 != ch2) {
            return ch1 < ch2 ? -1 : 1;
        }
        suffix1++;
        suffix2++;
    }
    return suffix1 > suffix2 ? -1 : 1;
}
```

</td><td>

```python
def compareSuffixes(self, suffix1: int, suffix2: int) -> int:
    if suffix1 == suffix2:
        return 0

    text = self.text
    end = len(text)
    while suffix1 < end and suffix2 < end:
        char1 = text[suffix1]
        char2 = text[suffix2]
        if char1 != char2:
            return -1 if char1 < char2 else 1
        suffix1 += 1
        suffix2 += 1

    return -1 if suffix1 > suffix2 else 1

```

</td></tr></table>

Take your time to really understand how the method works! Try it manually on some suffix examples (e.g., from the ABRACADABRA text).

For example, notice that the return statement after the while loop (`suffix1 > suffix2`) uses a greater-than comparison, and not smaller-than. Try to figure out why: when is that line reached and why is it necessary to use greater-than?

### Example texts

The **texts** directory has example text files for you to index. They are generated from the British National Corpus (BNC), a large collection of English-language text collected from all kinds of spoken and written sources.

| Text file                |   Sentences |        Words |    Characters | File size |
|:-------------------------|------------:|-------------:|--------------:|----------:|
| **bnc-tinyest.txt**      |          30 |        ≈ 500 |       ≈ 2,500 |     3k    |
| **bnc-tinyer.txt**       |         100 |      ≈ 2,000 |      ≈ 10,000 |    10k    |
| **bnc-tiny.txt**         |         300 |      ≈ 5,000 |      ≈ 30,000 |    30k    |
| **bnc-smallest.txt**     |       1,000 |     ≈ 17,000 |      ≈ 90,000 |    90k    |
| **bnc-smaller.txt**      |       3,000 |     ≈ 58,000 |     ≈ 320,000 |   320k    |
| **bnc-small.txt**        |      10,000 |    ≈ 190,000 |   ≈ 1,100,000 |     1M    |
| **bnc-medium.txt.gz**    |      30,000 |    ≈ 450,000 |   ≈ 2,500,000 |     1M    |
| **bnc-large.txt.gz**     |     100,000 |  ≈ 1,500,000 |   ≈ 8,800,000 |     3M    |
| **bnc-larger.txt.gz**    |     300,000 |  ≈ 4,700,000 |  ≈ 27,000,000 |     9M    |
| **bnc-largest.txt.gz**\* |   1,000,000 | ≈ 17,000,000 | ≈ 100,000,000 |    34M    |
| **bnc-huge.txt.gz**\*    |   2,000,000 | ≈ 36,000,000 | ≈ 210,000,000 |    73M    |
| **bnc-full.txt.gz**\*    | ≈ 6,000,000 | ≈ 97,000,000 | ≈ 570,000,000 |   195M    |

The texts marked with \* are too large to fit in the git repository, but you can download them from Canvas if you want: just go to the Files section and navigate to "lab-data/text-indexing". (Note that this is not necessary to complete the lab).

Note that the larger files are stored compressed in [gzip format](https://en.wikipedia.org/wiki/Gzip). But you don't have to do anything with them – the SuffixArray class can read both plain and compressed text files. (See the method `loadText` if you're interested how this works).

#### About the BNC corpus (you can skip this if you want)

The original BNC corpus can be downloaded from here: <http://www.natcorp.ox.ac.uk>

To create the example texts, we first converted the corpus to a UTF-8 encoded text file, with one sentence per line. Then we converted the file to pure ASCII using the [Unidecode](https://pypi.org/project/Unidecode/) library. The reason for this is so you won't have any encoding problems when searching for texts.

After that we selected 2,000,000 lines (=sentences) from the BNC, starting in line 2,895,763. This is the first sentence in Shakespeare's "The merchant of Venice". The merchant is followed by a scientific text about text recognition (starting in line 2,899,626), and then by several other texts.

So, the file **bnc-huge.txt.gz** starts with The merchant of Venice. All the other files (except **bnc-full.txt.gz**) consist of the N first lines of that file, so all example files start with The merchant of Venice:

```
CHAPTER ONE
'No,' said Sally-Anne McAllister dazedly.
'No, please, no,' and she struggled fiercely against the arms which held her – a man's, she noted, and that was enough to start her struggling even harder.
She would not be held by a man ever again.
No, not at all, and then, even in her confused state, her mind shied away from the reasons for her distaste, and she found herself saying even through her pain and shock, 'I will not think about that, I will not,' and so saying she stopped struggling and sank back into oblivion once more .
The next time she returned to consciousness she discovered that the whole right side of her face was numb, and that was all she registered.
The memory of being held in a man's hard arms had disappeared.
Her eyes opened; she was on her back.
Above her she saw a ceiling, grey and white, a plaster rose from which depended a gas-light inside a glass globe, engraved with roses.
She heard voices which at first made little sense, could not, for the moment, think where she might be or even who she was.
...
```

### The class `SuffixArray`

The class `SuffixArray` represents the search index. Apart from the attributes `text` for the text to search, it has a list `index` of integers that represents the sorted suffix array.

The core methods of `SuffixArray` are as follows:
* `setText`: sets the text to a given string.
* `generateRandomText`: generates a random string of a given size.
* `loadText`: loads the text from a text file (can be ´.gz´ compressed).
* `size`: returns the size of the text (the number of characters).
* `compareSuffixes`: helper method for comparing suffixes, described earlier. (You will use this in your sorting implementations).
* `linearSearch`: searches for a string in the text, using a slow linear search
* `binarySearch`: searches for a string using fast binary search in the suffix array. (To be implemented in part 4).
* `swap`: a helper method that swaps two indices in the suffix array. (You will use this in your sorting implementations).
* `buildIndex`: builds the sorted search index by calling a `SuffixSorter`.
* `saveIndex`: writes the built search index to disk. The filename used is that of the text file, with the suffix `.ix` added.
* `loadIndex`: reads a previously built search index from disk. This is much faster than building it from scratch.
* `checkIndex`: a helper method that checks if the final search index is sorted according to the suffixes. This is called by **BuildIndex.java/build_index.py**.
* `print`: a helper method that prints (a portion of) the suffix array. Used for debugging.

### Tasks for part 2: Create a suffix array manually

Manually create suffix arrays:
- from the string "SIRAPIPARIS"
- from the string "AAAAAAAAAA"

How do the resulting arrays look like? Write down your answers in **answers.txt**.


## Parts 3, 4 and 6: Implementing some sorting algorithms

We represent each sorting algorithm as extensions of the abstract class `SuffixSorter` (which is defined in **SuffixSorter.java/suffix_sorter.py**). There are the following implementations available:

- **BuiltinSort.java / builtin_sort.py**: this is already implemented
- **InsertionSort.java / insertion_sort.py**: to be completed in part 3
- **Quicksort.java / quicksort.py**: to be completed in part 4
- **MultikeyQuicksort.java / multikey_quicksort.py**: to be completed in part 6

These sorting algorithms take a `SuffixArray` as input, and sorts its `index` according to the suffixes as described earlier.

There is also a helper file **PivotSelector.java/pivot_selector.py**, which implements several different pivot selection strategies for quicksort (see part 4 below).

### The progress bar

There is a helper class `ProgressBar` (defined in file **ProgressBar.java/progress_bar.py**), which shows a progress bar so that you can see that the sorting is progressing. This is heavily inspired from the Python [tqdm](https://tqdm.github.io) library, but you don't have to install anything to use the `ProgressBar`.

All uses of the progress bar are already in the skeleton code, so you shouldn't have to think about it – hopefully it works out of the box.

### Running the program

Run the program **BuildIndex.java/build_index.py** to create a suffix array for a given text. You have to provide the text file and specify which algorithm you want to use. E.g., like this:

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```
$ java BuildIndex --textfile ../texts/bnc-medium.txt.gz --algorithm 
Reading 2503569 chars from '../texts/bnc-medium.txt.gz' took 0.09 seconds.
Building index took 2.41 seconds.
Checking index took 0.16 seconds.
Saving index to '../texts/bnc-medium.txt.gz.ix' took 0.14 seconds.
In total the program took 2.83 seconds.
```

</td><td>

```
$ python build_index.py -f ../texts/bnc-small.txt -a builtin
Reading 1081510 chars '../texts/bnc-small.txt' took 0.00 seconds.
Building index took 23.83 seconds.
Checking index took 3.60 seconds.
Saving index to '../texts/bnc-small.txt.ix' took 0.06 seconds.
In total the program took 27.49 seconds.
```

</td></tr></table>

If you want to know which alternatives you have, you can give the argument `--help`:

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```
$ java BuildIndex --help
Usage: java BuildIndex [-h] --textfile TEXTFILE --algorithm {quicksort,builtin,multikey,insertion} [--pivot {first,random,median,middle,adaptive}]

Build an inverted search index.

Options:
  -h, --help                  show this help message and exit
  --textfile TEXTFILE, -f TEXTFILE
                              text file (utf-8 encoded)
  --algorithm {quicksort,builtin,multikey,insertion}, -a {quicksort,builtin,multikey,insertion}
                              sorting algorithm
  --pivot {first,random,median,middle,adaptive}, -p {first,random,median,middle,adaptive}
                              pivot selectors (only for quicksort algorithms)
```

</td><td>

```
$ python build_index.py --help
usage: build_index.py [-h] --textfile TEXTFILE --algorithm {insertion,quicksort,multikey,builtin} [--pivot {first,middle,random,median,adaptive}]

Build an inverted search index.

optional arguments:
  -h, --help            show this help message and exit
  --textfile TEXTFILE, -f TEXTFILE
                        text file (utf-8 encoded)
  --algorithm {insertion,quicksort,multikey,builtin}, -a {insertion,quicksort,multikey,builtin}
                        sorting algorithm
  --pivot {first,middle,random,median,adaptive}, -p {first,middle,random,median,adaptive}
                        pivot selectors (only for quicksort algorithms)
```

</td></tr></table>


## Part 3: Insertion sort

Your first sorting task is to complete the `sort` method in the class `InsertionSort`. You should implement the in-place version of insertion sort, not allocating any additional memory.

<details>
<summary>
Spoiler 1
</summary>

To perform swaps, you can use the `SuffixArray` method `swap(i, j)`.
To compare array values, use the `SuffixArray` method `compareSuffixes`.
</details>

<details>
<summary>
Spoiler 2
</summary>

Don't use recursion. Two nested loops will do.
</details>

<details>
<summary>
Spoiler 3
</summary>

The outer loop is already implemented with an index `i` range from 0 to the size of the array. The inner loop should move the new element (at index `i`) backwards to its correct place according to the ordering. For example, this can be done by repeatedly swapping with the element before it.
</details>

<details>
<summary>
Spoiler 4
</summary>

The course book has pseudocode for insertion sort.
</details>

### Task for part 3: Testing insertion sort

The class `InsertionSort` has a main method with some basic tests. Feel free to add your own tests!

Now you should be able to build suffix arrays for the tiny BNC texts. Compile and run **BuildIndex.java/build_index.py** with the algorithm "insertion", and the text file of your choice. Then answer the following question in **answers.txt**:

- *How long time does it take to insertion sort the suffix array for each of the tiny files?*

If it takes less than 10 seconds to sort **bnc-tiny.txt** you should try **bnc-smallest.txt** too. And if that goes like a charm you might have some luck with **bnc-smaller.txt** too.


## Part 4: Quicksort

Your second sorting task is to complete the implementation of quicksort in **Quicksort.java/quicksort.py**. To help you structure your code, we have created a skeleton with two methods:

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```java
public void quicksort(int start, int end)
public int partition(int start, int end)
```

</td><td>

```python
def quicksort(self, start: int, end: int)
def partition(self, start: int, end: int) -> int
```

</td></tr></table>

Both methods are already started with some initialisations, but you have to finish them both.

You can choose which partitioning scheme to use in `partition`. We recommend the one taught in the course (see the course material and Spoiler 2 below).

Note the call to `pivotIndex` in `partition`. The class `Quicksort` uses a *pivot selection strategy* (see the interface `PivotSelector`) that can be specified by the method `setPivotSelector`.

We have already implemented a variety of pivot selection strategies – see **PivotSelector.java/pivot_selector.py** for more information.
* `TakeFirstPivot`: always pick the first element as pivot,
* `TakeMiddlePivot`: always pick the middle element as pivot,
* `RandomPivot`: pick a random pivot,
* `MedianOfThreePivot`: take the median of the first, middle, and last element,
* `AdaptivePivot`: adaptive strategy that takes the size of the range into account.

The same reminders as for insertion sort apply:
* To perform swaps, you can use the `SuffixArray` method `swap(i, j)`.
* To compare array values, use the `SuffixArray` method `compareSuffixes`.

Plus an important note:
* The first index in the given range is `start` and the last index is `end - 1`. This is different from the course book, where quicksort and partition uses inclusive intervals. 

<details>
<summary>
Spoiler 1
</summary>

Use recursion to sort the left and right parts of the partition in `quicksort`.
</details>

<details>
<summary>
Spoiler 2
</summary>

The partition scheme of the course works as follows.
- First swap the pivot with the first element.
- Initialize `lo = from + 1` and `hi = to - 1`.
- Advance `lo` forward and `hi` backward while their elements are in the correct position.
- Once we reach a conflict on both sides, we swap and advance.
- Eventually, `lo` and `hi` cross.
- Finally, where should the pivot go?
</details>

<details>
<summary>
Spoiler 3
</summary>

The course book has pseudocode for quicksort.
</details>

### Task for part 4: Testing quicksort

Just like `InsertionSort`, the class `Quicksort` has a main method with some basic tests. Feel free to add your own tests!

Now you should be able to build suffix arrays for medium-to-large BNC texts. Run **BuildIndex.java/build_index.py** with the algorithm "quicksort", and the text file of your choice.

You can e.g. start with the file **bnc-smallest.txt**, and if that goes smoothly continue with larger and larger files. You can stop when compiling takes longer than 20–30 seconds. Finally you can answer the following question in **answers.txt**:

- *How long time does it take to quicksort the suffix array for each of the three largest BNC files that you tried?*


## Part 5: Searching using the suffix array

Here, you will implement the following function in the file **BinarySearch.java/binary_search.py**:

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```java
public static int binarySearchFirst(SuffixArray sa, String value)
```

</td><td>

```python
def binarySearchFirst(sa: SuffixArray, value: str) -> int
```

</td></tr></table>

This function finds the *first* occurrence of `value` in the suffix array. That is, not the first occurrence in the text by position, but the alphabetically smallest suffix that starts with `value`. 

If we know this it's an easy task to iterate through all occurrences of `value`, as explained in the explanation about suffix arrays above. This part is already implemented as the method `binarySearch` of the class `SuffixArray`:

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```java
    public Iterable<Integer> binarySearch(String value) {
        int first = BinarySearch.binarySearchFirst(this, value);
        return new Iterable<>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<>() {
                    int i = first;
                    @Override
                    public boolean hasNext() {
                        if (i < 0 || i >= index.length)
                            return false;
                        int start = index[i];
                        int end = start + value.length();
                        return value.equals(text.substring(start, end));
                    }
                    @Override
                    public Integer next() {
                        if (hasNext())
                            return index[i++];
                        throw new NoSuchElementException();
                    }
                };
            }
        };
    }
```

</td><td>

```python
def binarySearch(self, value: str) -> Iterable[int]:
    first = binarySearchFirst(self.index, self.text, value)
    if first < 0: 
        return
    for i in range(first, self.size()):
        start = self.index[i]
        end = start + len(value)
        if value == self.text[start : end]:
            yield start
        else:
            break
```

</td></tr></table>

Notice that just as for the `linearSearch` method, the Java code is much clumsier than the Python code. This is because Python supports generator functions with the `yield` keyword, while in Java we have to implement the Iterator ourselves with the `hasNext` and `next` methods.

But the general idea is simple: first we find the position of the smallest suffix matching `value` – this position is called `first`. Then we continue yielding new results (suffix positions) until the suffix doesn't match `value` anymore.

### Testing your binary search implementation

Just like previously, **BinarySearch.java/binary_seach.py** has a main function with some basic tests. Feel free to add your own tests!

Finally you can reap the fruits of your labor. Run the file **SearchIndex.java/search_index.java** with a text file for which you have previously built an index.

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```
$ java SearchIndex --textfile ../texts/bnc-medium.txt.gz
Reading 2503569 chars from '../texts/bnc-medium.txt.gz' took 0.11 seconds.
Loading the index took 0.06 seconds.
Search key (ENTER to quit): 
```

</td><td>

```
$ python search_index.py --textfile ../texts/bnc-medium.txt.gz
Reading 2503569 chars '../texts/bnc-medium.txt.gz' took 0.02 seconds.
Loading the index took 0.10 seconds.
Search key (ENTER to quit): 
```

</td></tr></table>

If everything went well, you should now have a blazingly fast text search interface to the corpus you selected. If it takes longer than a hundredth of a second, you probably have some bug in your code.

Here are some concrete test cases for **bnc-medium.txt.gz**.

There should be exactly 7 matches for the string "University" (note the capital U):
```
Search key (ENTER to quit): University
Searching for 'University':
  746607:   quality of Rembrandts, and the private |University| College at Buckingham certifies educati
  563816:  l British dictionary publishers (Oxford |University| Press, Longmans and Chambers) are curre
 1916509:   he?' 'He is the Vice Chancellor of the |University| of Bridport,' said Ellen. There was a s
 1955291:   in 1991, written by Gene Lerner of the |University| of California at Santa Barbara, on what
  763008:  y Professor David Newberry of Cambridge |University| of the efficient way to achieve a 30 pe
  936252:  rent countries, George Yarrow of Oxford |University| reached the following conclusion. Priva
 1959662:   paper by Deborah Tannen, of Georgetown |University|, whose study of repetition in conversat
Finding 7 matches took 0.00 seconds.
```

You can also search for multiple-word strings:
```
Search key (ENTER to quit): speech recognition system
Searching for 'speech recognition system':
  458415:  uction of a large vocabulary continuous |speech recognition system| in 1972. The group pioneered the use of
  421823:  baiks/). Therefore training a connected |speech recognition system| with isolated words may not be satisfac
  423996:  ognition of speech. Currently available |speech recognition system|s impose a selection of constraints on t
  424198:  r these constraints do not apply to all |speech recognition system|s): Limited vocabulary The vocabulary si
Finding 4 matches took 0.00 seconds.
```

Or for parts of words:
```
Search key (ENTER to quit): abra
Searching for 'abra':
 1225241:  ly saw its waxy flowers bold as a candel|abra|. A horse chestnut. The trunk was studde
 1335083:  nd she lit a seven branched olive candel|abra|. There was a silver goblet in the centr
 1841942:  k and white mixture between collie and l|abra|dor, trotted happily towards me. It look
 1414916:  ared publication, dealing in hard fact, |abra|sive as Maggie herself was abrasive. She
 1414947:  rd fact, abrasive as Maggie herself was |abra|sive. She had been a feature writer here
Finding 5 matches took 0.00 seconds.
```

Wonderful, isn't it? Note how fast the query is! You could do almost a thousand queries per second. And most of the query time is probably just used for printing the results.

### Questions for part 5

In the example searches above, the first number that is printed on each line is the position of each search result. As you can see these numbers are not ordered, but they seem to be random.

- *Why do you think the results are not shown in increasing order of position?*

Now, search for a non-existing string (e.g., "this-does-not-exist") in the largest text file for which you have built a suffix array. (This should be at least **bnc-medium.txt.gz** if you use Python, and at least **bnc-large.txt.gz** if you use Java.)

Do this search in two ways – one using naive *linear search* (using the command line option `-l`), and one using your own *binary search*.

- *How long time does it take to search for a non-existing string using linear search, and using binary search, respectively?*
  (Don't include the time it takes to read the text and the index)

Write your answers to these questions in **answers.txt**.

## Part 6: Multi-key quicksort

Using quicksort to build the search index is already quite fast, but we are going to make it even faster. You will implement a version of quicksort that is particularly fast at sorting a list of sequences *in lexicographic order*. And a suffix array is exactly that – a list of sequences – so this new quicksort variant should be a perfect match for us.

In the end your implementation should be around twice as fast as your quicksort from part 3. If you don't see a drastic improvement you probably have a bug somewhere.

### Lexicographic order

Suppose we have a type T with an ordering (e.g., numbers or characters). This induces an ordering on the sequences of type T: given sequences x and y, we just look for the first difference between them. For example, if x is [4, 1, 7, 6] and y is [4, 1, 6, 8], then x > y because the first position where the sequences differ is index 2, and x[2] = 7 is bigger than y[2] = 6. We call this position within the lists x and y, an *offset*.

In this lab we sort strings, but strings are really just sequences of characters. When comparing two strings we can find the first offset where the characters differ and compare that character. This is how all programming languages implement string comparison, and it is how the method `compareSuffixes` is implemented (see the explanation about suffix arrays above). 

### Multi-key quicksort

Multi-key quicksort is a version of quicksort optimized for types with a lexicographic ordering. You will implement it in **MultikeyQuicksort.java/multikey_quicksort.py**.

There are two main differences over plain quicksort:

* We never compare strings fully. Instead, we only ever compare them at some offset. Initially, we compare strings only by their first character. But as the algorithm goes on, we will start comparing strings also by their second character offset, third offset, and so on.

* Instead of partitioning into a left part and a right part, we partition into *three parts*. The middle part will contain all the elements that compare equal to the pivot (for the chosen offset).

<table><tr><th>Java</th><th>Python</th></tr><tr><td>

```java
public void multikeyQuicksort(int start, int end, int offset)
public IndexPair partition(int start, int end, int offset)
```

</td><td>

```python
def multikeyQuicksort(self, start: int, end: int, offset: int)
def partition(self, start: int, end: int, offset: int) -> Tuple[int, int]
```

</td></tr></table>

Note that since `partition` divides the array into three parts, it has to return two indices. In Java we return an `IndexPair` and in Python it's a simple tuple `(i, j)`.

Also, just as for plain quicksort, the variable `end` points to the element *after* the last element in the interval.

### Partitioning

The pivot selection step works the same as in quicksort. But now we won't use the whole suffix string as the pivot – instead we will use a single *pivot character* from the given offset of the pivot string.

Initially, we partition the array by comparing only the first characters of the strings (offset 0). This gives us three parts:
* the left part has elements where the first character is smaller than the pivot character,
* the middle part has elements where the first character is the same as the pivot character,
* the right part has elements where the first character is larger than the pivot character.

In the general case, `partition` is given the offset in addition to the start and end indices. So we use that offset to find the pivot character, and the respective characters from the elements in the array. 

There is a convenience method `getCharAtOffset` which takes a suffix index and an offset as arguments and returns the character at that offset.

Now, how do we actually partition into three parts? Just as for normal quicksort, this should be in-place – i.e., not use a helper array. Feel free to look at some of the below spoilers if you are out of ideas.

<details>
<summary>
Spoiler 1
</summary>

Start as usual by swapping the pivot with the first element of the range.
</details>

<details>
<summary>
Spoiler 2
</summary>

Let's call the current range `start` and `end`. Eventually, we want a range `middleStart` and `middleEnd` for the middle part of the partition. This should include the pivot. The range from `start` and to `middleStart` will be the left part and the range from `middleEnd` to `end` will be the right part.
</details>

<details>
<summary>
Spoiler 3
</summary>

Initialize `middleStart = start` and `middleEnd = end`. You need to traverse all the elements from `middleStart` and `middleEnd`, compare each to the pivot in the position under consideration, and depending on the result swap it into its parts of the partition. We will update to update some of the variables such as `middleStart` and `middleEnd` to account for changes in the partition sizes.
</details>

<details>
<summary>
Spoiler 4
</summary>

Say we use `i` as the index to start at `middleStart`. (Actually, we can start at `middleStart + 1`. Why?) We process the element at `i` until we reach `i == middleEnd`. Inside the loop, the ranges have to following meaning:
* from `start` to `middleStart`: the left part so far
* from `middleStart` to `i`: the middle part so far
* from `i` to `middleEnd`: still to be processed
* from `middleEnd` to `end`: the right part so far
</details>

<details>
<summary>
Spoiler 5
</summary>

Suppose we process the element at `i`. Let `chr = getCharAtOffset(i, offset)`. What we should do depends on `chr`:
* If `chr < pivotChar`, the element belongs in the left part.
* If `chr == pivotChar`, the element belongs to the middle part.
* If `chr > pivotChar`, the element belongs to the right part.

In each case, we may use a swap to update the three parts of the partition so far with the new element. How do the variables `middleStart`, `middleEnd`, `i` change?
</details>

### Quicksorting

Just like in quicksort, we handle the left and right part recursively, calling the same method with a smaller range. But for the middle part, we can do something more efficient. Since we already know that all elements in the middle part have the same first character (offset=0), we can move on to comparing their *second characters* (offset=1).

To be able to recurse also in this case, we need to add a *comparison offset* parameter (called `offset` in `MultikeyQuicksort`) to the sorting method. Then the general pattern is:
* For the left and right part, we keep the same comparison offset in the recursive call.
* For the middle part, we increase the comparison offset by one.

### Task for part 6: Testing your implementation

Just like `Quicksort`, the `MultikeyQuicksort` has a main method with some basic tests. Feel free to add your own tests!

Now you should be able to build suffix arrays for medium-to-large BNC texts. Run **BuildIndex.java/build_index.py** with the algorithm "multikey", and the text file of your choice.

Comparing with normal Quicksort, your Multikey implementation should be around twice as fast. If you don't see an improvement, please talk to a TA. (It should even be faster than using Java's/Python's builtin sorting algorithm – you can try this by giving "builtin" as the algorithm.)

Answer the following question in **answers.txt**:

- *How long time does it take for Quicksort and Multikey Quicksort, respectively, to sort the suffix array for the largest BNC files that you tried?*


## Part 7: Empirical complexity analysis

In this final part you will do some empirical experiments to see if the actual runtime complexity of the algorithms are as predicted.

The sorting classes (**InsertionSort**, **Quicksort** and **MultikeyQuicksort**) all have main functions where you can write tests to run. 

To be able to test the complexity of your implementations empirically you have experiment with different text sizes. The method `generateRandomText` in the class **SuffixArray** lets you create a random text of a certain size, which you can use for this purpose. You can e.g. modify the code commented as "example performance tests" however you like (in the main function of the classes **InsertionSort**, **Quicksort** and **MultikeyQuicksort**).

- *Deduce the empirical complexity of each of the sorting implementations.*

Run at least 10 experiments with different text sizes and note the time it takes to sort the suffix array for each size. Then try to deduce what computational complexity your implementation has. Do this for **InsertionSort**, **Quicksort**, and **MultikeyQuicksort**. 

<details>
<summary>
Spoiler 1
</summary>

You can use a curve fitting tool. It can e.g. be the `scipy` package in Python, or some online tool such as <http://curve.fit/>, or even Excel or Google Spreadsheet.
</details>

<details>
<summary>
Spoiler 2
</summary>

Try to fit the data to a linear curve, to a quadratic curve, and perhaps to a linearithmic curve (i.e., `y = A + B * x * log(x)`), and measure which has the least error. 

Note that Excel and Google Spreadsheets cannot fit a linearithmic curve, only logarithmic, linear, polynomial and exponential curves.
</details>

<details>
<summary>
Spoiler 3
</summary>

It will probably be extremely difficult to see a difference between linear complexity (*O*(*n*)) and linearithmic (*O*(*n* log *n*)). So don't be alarmed if the curve fitter suggests any of the two.
</details>

Did your experimental results match with your expectations?

- *Vary the size of the alphabet.*

It's enough if you run this experiment on only one sorting algorithm, so let's use quicksort. The experiment code that you used above used an alphabet "ABCD". Generate a random text of some size and sort it. Now vary the size of the alphabet and run the same experiment (with the same text sizes). How does the sorting time vary when you vary the alphabet size? Try to come up with an explanation of the differences.

**Note**: You will notice that a one-letter alphabet is a special case! Why do you think this so much different from other alphabets?

<details>
<summary>
Spoiler
</summary>

It's probably enough to experiment with only two larger alphabet sizes and two smaller.
</details>


## Submission

Double check:
* Have you answered the questions in **answers.txt**?
  - don't forget the ones in the appendix
  - and don't forget to specify your programming language
* Have you tested your code with **Robograder**?

Read in Canvas how to submit your lab.


## Optional tasks

* When you run multi-key quicksort on **bnc-large.txt.gz** or larger, you may experience a stack overflow error. Which code path do you think a high level of nested calls comes from? How can you fix this?

* Run more experiments with random texts on different-size alphabets. Can you deduce a runtime complexity in terms of the two variables N (the size of the text) and V (the size of the alphabet)?

* When you run multi-key quicksort on a list that contains duplicates, your code will run forever or cause a stack overflow error. Fortunately, this can never happen for a suffix array (why?). How can you fix your implementation so that it also works with duplicates?


## Literature

- Wikipedia explains [suffix arrays](https://en.wikipedia.org/wiki/Suffix_array).

- Sedgewick & Wayne (2011) has a full chapter on string algorithms (chapter 5), including text searching. They even have a section about suffix arrays (in chapter 6 "Context").

- Wikipedia explains [multi-key quicksort](https://en.wikipedia.org/wiki/Multi-key_quicksort). But it gives away the pseudocode – try to first build your own version.

- [Bentley & Sedgewick (1994)](http://akira.ruc.dk/~keld/teaching/algoritmedesign_f04/Artikler/04/Bentley99.pdf) is the main research article about multi-key quicksort. Among other things is shows that multi-key quicksort it is isomorphic to ternary search trees, in the same way as quicksort is isomorphic to tries.

- There are plenty of research on how to make even faster suffix sorting algorithms, and [Larsson & Sadakane (2007)](https://blogs.asarkar.com/assets/docs/algorithms-curated/Faster%20Suffix%20Sorting%20-%20Larsson+Sadakane.pdf) present one of them.

