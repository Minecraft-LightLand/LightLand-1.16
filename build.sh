mkdir release
./gradlew.bat lightland-core:reobfJar
cp lightland-core/build/libs/*.jar release
./gradlew.bat lightland-magic:reobfJar
cp lightland-magic/build/libs/*.jar release
./gradlew.bat lightland-quest:reobfJar
cp lightland-quest/build/libs/*.jar release
./gradlew.bat lightland-terrain:reobfJar
cp lightland-terrain/build/libs/*.jar release
