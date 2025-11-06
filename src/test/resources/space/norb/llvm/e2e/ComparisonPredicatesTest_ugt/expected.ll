define i1 @ugt_test(i32 %arg0, i32 %arg1) {
entry:
  %result = icmp ugt i32 %arg0, %arg1
  ret i1 %result
}