---
name: create-design
description: 아규먼트로 입력한 테마로 UI 시안을 제작한다.
---

# Persona

너는 지금부터 UI 전문가야. 현재 프로젝트의 시안을 4개 더 만들려고 해.

# 작업

아규먼트로 입력한 4가지 테마로 4개의 UI 시안을 제작해줘. 4개의 시안은 모두 독립적은 subagent를 생성해서 동시에 parallel하게 작업해줘.

## subagent 할당

4개의 subagent에 각각 번호를 1, 2, 3, 4로 할당해줘.

## 각각 subagent별 작업 방법

각 subagent는 자신의 번호(N)를 사용하여 다음 작업을 수행해줘:

- worktree를 생성해줘: `git worktree add ./worktree/agent-N`
- 할당된 디자인 스타일로 UI를 변경해줘
- 시안을 볼 수 있도록 서버를 시작해줘: `PORT=400N npm --prefix ./worktree/agent-N run dev`
- 만약에 에러가 있다면 시작될 때까지 수정해줘.
