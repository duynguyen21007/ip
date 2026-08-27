# Milo UI Test Plan

## Configuration

- Main class: `Milo`
- Source directory: `src/main/java`
- Java release: `25`

Each test case starts Milo in a fresh process. The runner compares complete standard output after normalizing platform line endings and trailing spaces. Leading whitespace remains significant. It stops immediately after the first failing case and records the visible session in `_temp/ui-test/session.log`.

## Test case: Add and list all task types

### Aim

Verify that todos, deadlines, and events retain their type-specific details and appear in insertion order.

### Inputs

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] borrow book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [D][ ] return book (by: Sunday)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [E][ ] project meeting (from: Mon 2pm to: 4pm)
                   Now you have 3 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] borrow book
                   2.[D][ ] return book (by: Sunday)
                   3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Save task list after changes

### Aim

Verify that adding, marking, and deleting tasks completes normally while each list change is persisted.

### Inputs

```text
todo read book
mark 1
delete 1
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/ 
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] read book
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][X] read book
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Mark a task as done

### Aim

Verify that `mark` changes the selected task to done and that `list` retains the status.

### Inputs

```text
todo read book
mark 1
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] read book
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][X] read book
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Unmark a completed task

### Aim

Verify that `unmark` reverses a task's done status and that `list` retains the change.

### Inputs

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] read book
                   -----------------------------------
                   -----------------------------------
                   OK, I've marked this task as not done yet:
                     [T][ ] read book
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] read book
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Recover from invalid commands

### Aim

Verify that an empty todo and an unknown command produce clear errors without terminating Milo.

### Inputs

```text
todo
blah
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   OOPS!!! A todo needs a description.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! I don't recognize that command :-(
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Reject incomplete typed tasks

### Aim

Verify that deadlines and events missing their required date/time fields are rejected.

### Inputs

```text
deadline return book
event project meeting /from Mon 2pm
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   OOPS!!! A deadline needs a description followed by /by and a date or time.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! An event needs a description, /from start, and /to end.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Rejected task creation preserves list state

### Aim

Verify that invalid task commands interleaved with valid additions do not consume list positions or alter later tasks.

### Inputs

```text
todo read book
todo
deadline submit report /by Friday
deadline prepare slides
event project meeting /from 2pm /to 4pm
event lunch /from noon
mark 3
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! A todo needs a description.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [D][ ] submit report (by: Friday)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! A deadline needs a description followed by /by and a date or time.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [E][ ] project meeting (from: 2pm to: 4pm)
                   Now you have 3 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! An event needs a description, /from start, and /to end.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [E][X] project meeting (from: 2pm to: 4pm)
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] read book
                   2.[D][ ] submit report (by: Friday)
                   3.[E][X] project meeting (from: 2pm to: 4pm)
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Invalid status changes preserve done states

### Aim

Verify that missing, non-numeric, zero, and out-of-range task numbers do not change task completion states between valid mark and unmark commands.

### Inputs

```text
todo first task
todo second task
mark 1
unmark 0
mark 3
unmark two
unmark 1
mark
mark 2
unmark
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] first task
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] second task
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] first task
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 0.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 3.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: unmark 2
                   -----------------------------------
                   -----------------------------------
                   OK, I've marked this task as not done yet:
                     [T][ ] first task
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: mark 2
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] second task
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: unmark 2
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] first task
                   2.[T][X] second task
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Whitespace and extra command text preserve state

### Aim

Verify that leading whitespace is accepted while an unexpected command suffix and a blank command are rejected without affecting an existing task.

### Inputs

```text
   todo trim leading spaces
list extra
list

mark 1
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] trim leading spaces
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! I don't recognize that command :-(
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] trim leading spaces
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! I don't recognize that command :-(
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] trim leading spaces
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][X] trim leading spaces
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Delete a task from the middle of the list

### Aim

Verify that deleting a typed task reports the removed task, reduces the count, and shifts later tasks forward while preserving their done states.

### Inputs

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] read book
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [D][ ] return book (by: June 6th)
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
                   Now you have 3 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] join sports club
                   Now you have 4 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] borrow book
                   Now you have 5 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] read book
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [D][X] return book (by: June 6th)
                   -----------------------------------
                   -----------------------------------
                   Nice! I've marked this task as done:
                     [T][X] join sports club
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][X] read book
                   2.[D][X] return book (by: June 6th)
                   3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
                   4.[T][X] join sports club
                   5.[T][ ] borrow book
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
                   Now you have 4 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][X] read book
                   2.[D][X] return book (by: June 6th)
                   3.[T][X] join sports club
                   4.[T][ ] borrow book
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```

## Test case: Invalid deletions preserve list state

### Aim

Verify that missing, non-numeric, zero, and out-of-range delete arguments leave the list unchanged, while deleting the last and only remaining tasks works.

### Inputs

```text
todo keep first
todo remove second
delete
delete 0
delete three
delete 3
list
delete 2
delete 1
delete 1
list
bye
```

### Expected output

```text
 __  __ _ _
|  \/  (_) | ___
| |\/| | | |/ _ \
| |  | | | | (_) |
|_|  |_|_|_|\___/
-----------------------------------
Hello! I'm Milo.
How can I help you?
-----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] keep first
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Got it. I've added this task:
                     [T][ ] remove second
                   Now you have 2 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: delete 2
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 0.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! Please specify a task number, for example: delete 2
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 3.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   1.[T][ ] keep first
                   2.[T][ ] remove second
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] remove second
                   Now you have 1 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   Noted. I've removed this task:
                     [T][ ] keep first
                   Now you have 0 tasks in the list.
                   -----------------------------------
                   -----------------------------------
                   OOPS!!! There is no task numbered 1.
                   -----------------------------------
                   -----------------------------------
                   Here are the tasks in your list:
                   -----------------------------------
                   -----------------------------------
                   Bye, see you later!
                   -----------------------------------
```
