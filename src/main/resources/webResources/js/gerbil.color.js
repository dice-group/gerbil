/**
 * This hash function has been taken from here:
 * http://www.cse.yorku.ca/~oz/hash.html
 */
function sdbm(str) {
	var hash = 0;
	var c;
	for (i = 0; i < str.length; i++) {
		c = str.charCodeAt(i);
		hash = c + (hash << 6) + (hash << 16) - hash;
	}
	return hash;
}

function colorForString(str) {
	var hash = sdbm(str);
	var colorValue = hash % (1 << 24);
	var color = colorValue.toString(16);
	color = color.replace(/\-/, "");
	while (color.length < 6) {
		color = "0" + color;
	}
	return "#" + color;
}