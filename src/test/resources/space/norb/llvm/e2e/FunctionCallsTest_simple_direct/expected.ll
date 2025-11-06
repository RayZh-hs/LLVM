define i32 @add(i32 %a, i32 %b) {
entry:
  %result = add i32 %a, %b
  ret i32 %result
}

define i32 @main() {
entry:
  %call_result = call i32 @add(i32 5, i32 3)
  ret i32 %call_result
}