define i32 @urem(i32 %arg0, i32 %arg1) {
entry:
  %result = urem i32 %arg0, %arg1
  ret i32 %result
}