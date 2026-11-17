#!/bin/bash
# Хэсэг C — Fault seeding: гурван урсгалын гажилтыг НЭГ НЭГЭЭР тарьж,
# mvn test ажиллуулж, гаралтыг results/-д хадгалаад, кодыг git restore-оор буцаана.
# Мутацийг commit ХИЙХГҮЙ — скрипт дуусахад src/main цэвэр үлдэнэ.

cd "$(dirname "$0")" || exit 1
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
SRC=src/main/java/mn/edu/must/sqat/DataFlowTargets.java

check() {
  if grep -q "$1" "$SRC"; then echo "   мутаци хэрэгжсэн"; else echo "   !!! ХЭРЭГЖСЭНГҮЙ: $1"; fi
}

run() {
  echo ""
  echo "=================== $1 ==================="
  mvn test 2>&1 | tee "results/$1" | grep -E 'Tests run:|BUILD' | tail -3
}

# --- M1: streak = streak + 1 мөрийг устгах (missing update) -----------
perl -pi -e 's{^(\s*)streak = streak \+ 1;}{$1// streak = streak + 1;   // M1}' "$SRC"
check '// streak = streak + 1;   // M1'
run mvn-test-M1.txt
git restore "$SRC"

# --- M2: best = streak  ->  best = present (wrong variable) -----------
perl -pi -e 's{best = streak;}{best = present;}' "$SRC"
check 'best = present;'
run mvn-test-M2.txt
git restore "$SRC"

# --- M3: үхсэн def нэмэх (dead definition) ----------------------------
perl -pi -e 's{^(\s*)int score = 0;}{$1int total = present + best;   // M3: dead definition\n$1int score = 0;}' "$SRC"
check 'int total = present + best;'
run mvn-test-M3.txt
git restore "$SRC"

echo ""
echo "=================== ДҮН ==================="
for f in results/mvn-test-M1.txt results/mvn-test-M2.txt results/mvn-test-M3.txt; do
  printf "%-22s %s\n" "$(basename $f)" "$(grep -o 'Tests run: [0-9]*, Failures: [0-9]*, Errors: [0-9]*, Skipped: [0-9]*' $f | tail -1)"
  grep -o 'BonusTest\.[a-zA-Z0-9_]*:[0-9]* .*' "$f" | sed 's/^/   унав: /' | sort -u
done
echo ""
echo "Эх код цэвэр эсэх:"
git status --short "$SRC" | grep . || echo "   src/main өөрчлөгдөөгүй"
