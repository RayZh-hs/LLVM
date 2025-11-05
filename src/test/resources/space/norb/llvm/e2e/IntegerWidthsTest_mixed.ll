define i64 @mixed_width_ops(i8 %arg0, i16 %arg1, i32 %arg2, i64 %arg3) {
entry:
  %temp1 = sext i8 %arg0 to i64
  %temp2 = sext i16 %arg1 to i64
  %temp3 = sext i32 %arg2 to i64
  %temp4 = add i64 %temp1, %temp2
  %temp5 = add i64 %temp4, %temp3
  %result = add i64 %temp5, %arg3
  ret i64 %result
}