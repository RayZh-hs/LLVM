define i1 @sge_test(i32 %arg0, i32 %arg1) {
entry:
  %result = icmp sge i32 %arg0, %arg1
  ret i1 %result
}