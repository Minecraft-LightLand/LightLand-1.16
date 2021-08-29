mkdir release
./gradlew lightland-core:reobfJar
cp lightland-core/build/libs/*.jar release
./gradlew lightland-magic:reobfJar
cp lightland-magic/build/libs/*.jar release
./gradlew lightland-quest:reobfJar
cp lightland-quest/build/libs/*.jar release
./gradlew lightland-terrain:reobfJar
cp lightland-terrain/build/libs/*.jar release
