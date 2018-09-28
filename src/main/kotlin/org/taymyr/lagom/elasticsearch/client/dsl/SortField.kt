package org.taymyr.lagom.elasticsearch.client.dsl

/**
 * <p>Example.</p>
 * <pre>
 *     public class AuctionEndSort implements SortField {
 *        {@literal @}JsonProperty("auctionEnd")
 *         Value auctionEnd = new Value();
 *        {@literal @}JsonCreator
 *         AuctionEndSort() {
 *         }
 *         static class Value{
 *            {@literal @}JsonProperty("order")
 *             String order= "desc";
 *            {@literal @}JsonProperty("unmapped_type")
 *             String safety = "boolean";
 *         }
 *      }
 * </pre>
 * <p>Result JSON</p>
 * <pre>
 *     {"auctionEnd" : { "order" : "desc", "unmapped_type": "boolean" }}
 * </pre>
 */
interface SortField