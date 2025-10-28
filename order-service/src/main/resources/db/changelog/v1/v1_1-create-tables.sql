CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_creation_date ON orders(creation_date);

CREATE TABLE items (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       price DECIMAL(10,2) NOT NULL CHECK (price >= 0)
);

CREATE INDEX idx_items_name ON items(name);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             item_id BIGINT NOT NULL,
                             quantity INTEGER NOT NULL CHECK (quantity > 0),

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_order_items_item
                                 FOREIGN KEY (item_id)
                                     REFERENCES items(id)
                                     ON DELETE RESTRICT,

                             CONSTRAINT unique_order_item
                                 UNIQUE (order_id, item_id)
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_item_id ON order_items(item_id);
