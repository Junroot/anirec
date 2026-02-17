#!/bin/bash
if [ $# -eq 0 ]; then
  echo "Error: 워크트리 이름을 입력해주세요!"
  return 1
fi

ARGUMENT=$1
WORKTREE_PATH="../worktree/$ARGUMENT"
if git worktree add "$WORKTREE_PATH"; then
  echo "워크트리 생성 성공: $WORKTREE_PATH"
  cd "$WORKTREE_PATH" || return 1
  echo "디렉터리 변경 완료 $(pwd)"
  claude
else
  echo "워크트리 생성에 실패했습니다."
  return 1
fi
