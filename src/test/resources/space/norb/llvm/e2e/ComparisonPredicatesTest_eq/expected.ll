define i1 @eq_test(i32 %arg0, i32 %arg1) {
entry:
  %result = icmp eq i32 %arg0, %arg1
  ret i1 %result
}