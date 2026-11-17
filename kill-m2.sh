#!/bin/bash
# Хэсэг C үргэлжлэл: M2-ийг унагах тест нэмсний дараа
#   (1) M2-г дахин тарьж mvn test -> results/mvn-test-M2-kill.txt
#   (2) кодыг буцааж эцсийн mvn verify -> results/mvn-verify-final.txt, jacoco-final.csv

cd "$(dirname "$0")" || exit 1
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
SRC=src/main/java/mn/edu/must/sqat/DataFlowTargets.java

echo "=== M2 дахин тарьж шинэ тестээр шалгах ==="
perl -pi -e 's{best = streak;}{best = present;}' "$SRC"
grep -q 'best = present;' "$SRC" && echo "   M2 хэрэгжсэн" || echo "   !!! M2 ХЭРЭГЖСЭНГҮЙ"
mvn test 2>&1 | tee results/mvn-test-M2-kill.txt | grep -E 'Tests run:|BUILD' | tail -3
git restore "$SRC"

echo ""
echo "=== Эх код сэргээгдсэн, эцсийн гүйлт ==="
mvn verify 2>&1 | tee results/mvn-verify-final.txt | grep -E 'Tests run:|BUILD' | tail -3
cp target/site/jacoco/jacoco.csv results/jacoco-final.csv

echo ""
echo "=================== ДҮН ==================="
printf "%-26s %s\n" "mvn-test-M2-kill.txt" "$(grep -o 'Tests run: [0-9]*, Failures: [0-9]*, Errors: [0-9]*, Skipped: [0-9]*' results/mvn-test-M2-kill.txt | tail -1)"
grep -o 'BonusTest\.[a-zA-Z0-9_]*:[0-9]* .*' results/mvn-test-M2-kill.txt | sed 's/^/   унав: /' | sort -u
printf "%-26s %s\n" "mvn-verify-final.txt" "$(grep -o 'Tests run: [0-9]*, Failures: [0-9]*, Errors: [0-9]*, Skipped: [0-9]*' results/mvn-verify-final.txt | tail -1)"
echo "jacoco-final.csv:"
tail -1 results/jacoco-final.csv | awk -F, '{printf "   Instruction %d/%d · Branch %d/%d · Line %d/%d  (BRANCH_MISSED=%d, LINE_MISSED=%d)\n",$5,$4+$5,$7,$6+$7,$9,$8+$9,$6,$8}'
echo ""
echo "Эх код цэвэр эсэх:"
git status --short "$SRC" | grep . || echo "   src/main өөрчлөгдөөгүй"
