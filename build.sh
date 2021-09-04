export JAVA_HOME=`/usr/libexec/java_home -v 1.8`
rm -rf release
mkdir release
./gradlew lightland-core:reobfJar
./gradlew lightland-magic:reobfJar
./gradlew lightland-quest:reobfJar
./gradlew lightland-terrain:reobfJar
cp lightland-core/build/libs/*.jar release
cp lightland-magic/build/libs/*.jar release
cp lightland-quest/build/libs/*.jar release
cp lightland-terrain/build/libs/*.jar release
rm -rf lightland-core/build/libs
rm -rf lightland-magic/build/libs
rm -rf lightland-quest/build/libs
rm -rf lightland-terrain/build/libs
