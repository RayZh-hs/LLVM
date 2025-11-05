define i64 @complex_cast_ops(i8 %arg0, i32 %arg1) {
entry:
  %temp1 = sext i8 %arg0 to i64
  %temp2 = zext i32 %arg1 to i64
  %temp3 = add i64 %temp1, %temp2
  %temp4 = trunc i64 %temp3 to i32
  %result = sext i32 %temp4 to i64
  ret i64 %result
}