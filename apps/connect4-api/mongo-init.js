db = db.getSiblingDB("connect4");
db.createCollection("batches");
db.createUser({
  user: "connect4",
  pwd: "f820YaWj06I3Y5vYe8lb",
  roles: [
    {
      role: "readWrite",
      db: "connect4"
    }
  ]
});
