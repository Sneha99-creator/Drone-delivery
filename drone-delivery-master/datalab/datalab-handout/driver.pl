#!/usr/bin/perl
#######################################################################
# driver.pl - CS:APP Data Lab driver
#
# Copyright (c) 2004, R. Bryant and D. O'Hallaron, All rights reserved.
# May not be used, modified, or copied without permission.
#
#######################################################################

use strict 'vars';
use Getopt::Std;

use lib ".";
use Driverlib;

# Generic settings 
$| = 1;      # Flush stdout each time
umask(0077); # Files created by the user in tmp readable only by that user
$ENV{PATH} = "/usr/local/bin:/usr/bin:/bin";

#
# usage - print help message and terminate
#
sub usage {
    printf STDERR "$_[0]\n";
    printf STDERR "Usage: $0 [-h] [-u userid]\n";
    printf STDERR "Options:\n";
    printf STDERR "  -h          Print this message.\n";
    printf STDERR "  -u userid   Send autoresult to Autolab as userid.\n";
    die "\n";
}

##############
# Main routine
##############

my $driverfiles = "tests.c,dlc,bddcheck";
my $login = getlogin() || (getpwuid($<))[0] || "unknown";
my $tmpdir = "/usr/tmp/datalab.$login.$$";
my $diemsg = "The files are in $tmpdir.";

my $userid;
my $infile;
my $autograded;

my $status;
my $inpuzzles;
my $puzzlecnt;
my $line;
my $blank;
my $name;
my $c_points;
my $c_rating;
my $c_errors;
my $p_points;
my $p_rating;
my $p_errors;
my $total_c_points;
my $total_c_rating;
my $total_p_points;
my $total_p_rating;
my $tops;
my $tpoints;
my $trating;
my $foo;
my $name;
my $msg;

my $result;

my %puzzle_c_points;
my %puzzle_c_rating;
my %puzzle_c_errors;
my %puzzle_p_points;
my %puzzle_p_ops;
my %puzzle_p_maxops;
my %puzzle_number;


# Parse the command line arguments
no strict;
getopts('hu:f:A');
if ($opt_h) {
    usage();
}

$infile = "bits.c";
$userid = "";

#####
# These are command line args that every driver must support
#

# Causes the driver to send an autoresult to the server on behalf of user
if ($opt_u) {
    $userid = $opt_u;
}

# Hidden flag that indicates that the driver was invoked by an autograder
if ($opt_A) {
    $autograded = $opt_A;
}

#####
# Drivers can also define an arbitary number of other command line args
#
# Optional hidden flag used by the autograder
if ($opt_f) {  
    $infile = $opt_f;
}




use strict 'vars';

################################################
# Compute the correctness and performance scores
################################################

# Make sure that an executable dlc exists
(-e "./dlc" and -x "./dlc")
    or  die "$0: ERROR: No executable dlc binary.\n";

# Make sure that an executable cbit exists
(-e "./bddcheck/cbit" and -x "./bddcheck/cbit")
    or  die "$0: ERROR: No executable cbit binary.\n";

#
# Set up the contents of the scratch directory
#
system("mkdir $tmpdir") == 0
    or die "$0: Could not make scratch directory $tmpdir.\n";
unless (system("cp $infile $tmpdir/bits.c") == 0) { 
    clean($tmpdir);
    die "$0: Could not copy file $infile to scratch directory $tmpdir.\n";
}
unless (system("cp -r \{$driverfiles\} $tmpdir") == 0) {
    clean($tmpdir);
    die "$0: Could not copy support files to $tmpdir.\n";
}

# Change the current working directory to the scratch directory
unless (chdir($tmpdir)) {
    clean($tmpdir);
    die "$0: Could not change directory to $tmpdir.\n";
}

#
# Generate a zapped (for coding rules) version of bits.c
#
print "1. Running './dlc -z' to identify coding rules violations.\n";
system("cp bits.c save-bits.c") == 0
    or die "$0: ERROR: Could not create backup copy of bits.c. $diemsg\n";
system("./dlc -z -o zap-bits.c bits.c") == 0
    or die "$0: ERROR: zapped bits.c did not compile. $diemsg\n";

#
# Run BDD checker to determine correctness score
#
print "\n2. Running './bddcheck/check.pl -g' to determine correctness score.\n";
system("cp zap-bits.c bits.c");
$status = system("./bddcheck/check.pl -g > btest-zapped.out 2>&1");
if ($status != 0) {
    die "$0: ERROR: BDD check failed. $diemsg\n";
}

#
# Run dlc to identify operator count violations
# 
print "\n3. Running './dlc -Z' to identify operator count violations.\n";
system("./dlc -Z -o Zap-bits.c save-bits.c") == 0
    or die "$0: ERROR: dlc unable to generated Zapped bits.c file.\n";

#
# Run btest to compute performance score
#
print "\n4. Running './bddcheck/check.pl -g -r 2' to determine performance score.\n";
system("cp Zap-bits.c bits.c");
$status = system("./bddcheck/check.pl -g -r 2 > btest-Zapped.out 2>&1");

if ($status != 0) {
    die "$0: ERROR: Zapped btest failed. $diemsg\n";
}

#
# Run dlc to get the operator counts on the zapped input file
#
print "\n5. Running './dlc -e' to get operator count of each function.\n";
$status = system("./dlc -W1 -e zap-bits.c > dlc-opcount.out 2>&1");
if ($status != 0) {
    die "$0: ERROR: bits.c did not compile. $diemsg\n";
}
 
#################################################################
# Collect the correctness and performance results for each puzzle
#################################################################

#
# Collect the correctness results produced by btest
#
%puzzle_c_points = (); # Correctness score computed by btest
%puzzle_c_errors = (); # Correctness error discovered by btest
%puzzle_c_rating = (); # Correctness puzzle rating (max points)
  
$inpuzzles = 0;      # Becomes true when we start reading puzzle results
$puzzlecnt = 0;      # Each puzzle gets a unique number
$total_c_points = 0;
$total_c_rating = 0; 

open(INFILE, "$tmpdir/btest-zapped.out") 
    or die "$0: ERROR: could not open input file $tmpdir/btest-zapped.out\n";

while ($line = <INFILE>) {
    chomp($line);

    # Notice that we're ready to read the puzzle scores
    if ($line =~ /^Score/) {
	$inpuzzles = 1;
	next;
    }

    # Notice that we're through reading the puzzle scores
    if ($line =~ /^Total/) {
	$inpuzzles = 0;
	next;
    }

    # Read and record a puzzle's name and score
    if ($inpuzzles) {
	($blank, $c_points, $c_rating, $c_errors, $name) = split(/\s+/, $line);
	$puzzle_c_points{$name} = $c_points;
	$puzzle_c_errors{$name} = $c_errors;
	$puzzle_c_rating{$name} = $c_rating;
	$puzzle_number{$name} = $puzzlecnt++;
	$total_c_points += $c_points;
	$total_c_rating += $c_rating;
    }

}
close(INFILE);

#
# Collect the performance results generated by the BDD checker
#
%puzzle_p_points = (); # Performance points

$inpuzzles = 0;       # Becomes true when we start reading puzzle results
$total_p_points = 0;  
$total_p_rating = 0;

open(INFILE, "$tmpdir/btest-Zapped.out") 
    or die "$0: ERROR: could not open input file $tmpdir/btest-Zapped.out\n";

while ($line = <INFILE>) {
    chomp($line);

    # Notice that we're ready to read the puzzle scores
    if ($line =~ /^Score/) {
	$inpuzzles = 1;
	next;
    }

    # Notice that we're through reading the puzzle scores
    if ($line =~ /^Total/) {
	$inpuzzles = 0;
	next;
    }

    # Read and record a puzzle's name and score
    if ($inpuzzles) {
	($blank, $p_points, $p_rating, $p_errors, $name) = split(/\s+/, $line);
	$puzzle_p_points{$name} = $p_points;
	$total_p_points += $p_points;
	$total_p_rating += $p_rating;
    }
}
close(INFILE);

#
# Collect the operator counts generated by dlc
#
#
open(INFILE, "$tmpdir/dlc-opcount.out") 
    or die "$0: ERROR: could not open input file $tmpdir/dlc-opcount.out\n";

$tops = 0;
while ($line = <INFILE>) {
    chomp($line);

    if ($line =~ /(\d+) operators/) {
	($foo, $foo, $foo, $name, $msg) = split(/:/, $line);
	$puzzle_p_ops{$name} = $1;
	$tops += $1;
    }
}
close(INFILE);

# 
# Print a table of results sorted by puzzle number
#
print "\n";
printf("%s\t%s\n", "Correctness Results", "Perf Results");
printf("%s\t%s\t%s\t%s\t%s\t%s\n", "Points", "Rating", "Errors", 
       "Points", "Ops", "Puzzle");
foreach $name (sort {$puzzle_number{$a} <=> $puzzle_number{$b}} 
	       keys %puzzle_number) {
    printf("%d\t%d\t%d\t%d\t%d\t\%s\n", 
	   $puzzle_c_points{$name},
	   $puzzle_c_rating{$name},
	   $puzzle_c_errors{$name},
	   $puzzle_p_points{$name},
	   $puzzle_p_ops{$name},
	   $name);
}

$tpoints = $total_c_points + $total_p_points;
$trating = $total_c_rating + $total_p_rating;

print "\nScore = $tpoints/$trating [$total_c_points/$total_c_rating Corr + $total_p_points/$total_p_rating Perf] ($tops total operators)\n";


#
# Send autoresult to server
#
$result = "$tpoints|$total_c_points|$total_p_points|$tops";
foreach $name (sort {$puzzle_number{$a} <=> $puzzle_number{$b}} 
	       keys %puzzle_number) {
    $result .= " |$name:$puzzle_c_points{$name}:$puzzle_c_rating{$name}:$puzzle_p_points{$name}:$puzzle_p_ops{$name}";
}

# Post the autoresult string to Autolab
&Driverlib::driver_post($userid, $result, $autograded);

# Clean up and exit
clean ($tmpdir);
exit;

#
# clean - remove the scratch directory
#
sub clean {
    my $tmpdir = shift;
    system("rm -rf $tmpdir");
}

