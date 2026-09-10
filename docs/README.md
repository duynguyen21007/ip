# Milo User Guide

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details

## Viewing task statistics

Enter `stats` to summarize all tasks currently in your list. This command works in both the graphical interface and the console.

```text
stats
```

For a list containing one completed todo, one pending deadline, and one pending event, Milo responds:

```text
Here are your task statistics:
Total: 3
Completed: 1
Pending: 2
Completion: 33.3%
Todos: 1
Deadlines: 1
Events: 1
```

* Completion is the completed count divided by the total, displayed as a percentage rounded to one decimal place. An empty list shows zero for every count and `0.0%` completion.
* Statistics include both completed and pending tasks in each type count. They reflect additions, marking, unmarking, and deletions immediately.
* Statistics always cover the full current list, even after `find`. Deleted tasks are excluded; completion dates and historical weekly counts are not tracked.
* Viewing statistics does not change tasks or save data. Existing task files remain compatible.
* Use lowercase `stats` without arguments. For example, `stats week` produces `OOPS!!! Use stats without additional arguments.`
