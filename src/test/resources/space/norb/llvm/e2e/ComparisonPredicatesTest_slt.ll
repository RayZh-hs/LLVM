define i1 @slt_test(i32 %arg0, i32 %arg1) {
entry:
  %result = icmp slt i32 %arg0, %arg1
  ret i1 %result
}