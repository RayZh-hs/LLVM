define i32 @multiply(i32 %a, i32 %b) {
entry:
  %result = mul i32 %a, %b
  ret i32 %result
}

define i32 @calculate(i32 %x, i32 %y, i32 %z) {
entry:
  %mul_result = call i32 @multiply(i32 %x, i32 %y)
  %final_result = add i32 %mul_result, %z
  ret i32 %final_result
}

define i32 @main() {
entry:
  %call_result = call i32 @calculate(i32 2, i32 3, i32 4)
  ret i32 %call_result
}