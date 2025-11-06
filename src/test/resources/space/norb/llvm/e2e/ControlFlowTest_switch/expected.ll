define i32 @switch_example(i32 %arg0) {
entry:
  switch i32 %arg0, label %default [
    i32 1, label %case1
    i32 2, label %case2
  ]

case1:
  br label %merge

case2:
  br label %merge

default:
  br label %merge

merge:
  %result = phi i32 [ 10, %case1 ], [ 20, %case2 ], [ 0, %default ]
  ret i32 %result
}