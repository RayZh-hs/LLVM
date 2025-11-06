define i32 @multiple_branches() {
entry:
  br label %block1

block1:
  br label %block2

block2:
  br label %block3

block3:
  ret i32 123
}