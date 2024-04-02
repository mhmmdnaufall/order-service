CREATE TABLE orders
(
    id           BIGINT       NOT NULL,
    order_number VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDb;

CREATE TABLE orders_seq
(
    next_val BIGINT
) ENGINE = InnoDb;

INSERT INTO orders_seq (next_val)
VALUES (1);

CREATE TABLE order_line_items
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    sku_code VARCHAR(100) NOT NULL,
    price    DECIMAL      NOT NULL,
    quantity INT          NOT NULL,
    order_id BIGINT       NOT NULL,
    CONSTRAINT fk_order_line_items_to_order
        FOREIGN KEY (order_id) REFERENCES orders (id),
    PRIMARY KEY (id)
) ENGINE = InnoDb;