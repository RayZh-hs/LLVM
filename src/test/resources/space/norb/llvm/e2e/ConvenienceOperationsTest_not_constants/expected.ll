define i32 @not_with_constants() {
entry:
  %not1 = xor i32 305419896, -1
  %not2 = xor i32 2271560481, -1
  %result = xor i32 %not1, %not2
  ret i32 %result
}