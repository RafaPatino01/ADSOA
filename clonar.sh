current_dir=${PWD##*/}
copy="SERVER"$$
cd ..
cp -R $current_dir $copy 
cd $copy
java -jar server-1.0-SNAPSHOT-jar-with-dependencies.jar