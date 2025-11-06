define i1 @uge_test(i32 %arg0, i32 %arg1) {
entry:
  %result = icmp uge i32 %arg0, %arg1
  ret i1 %result
}