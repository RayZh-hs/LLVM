define void @void_with_branches(i32 %arg0) {
entry:
  %condition = icmp eq i32 %arg0, 0
  br i1 %condition, label %block1, label %block2

block1:
  ret void

block2:
  ret void
}