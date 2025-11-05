define i32 @unconditional_branch() {
entry:
  br label %target

target:
  ret i32 42
}