define i8 @trunc(i32 %arg0) {
entry:
  %result = trunc i32 %arg0 to i8
  ret i8 %result
}