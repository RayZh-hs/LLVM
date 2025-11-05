define i32 @neg_with_constants() {
entry:
  %neg1 = sub i32 0, 42
  %neg2 = sub i32 0, -17
  %result = add i32 %neg1, %neg2
  ret i32 %result
}