// Copyright (c) 2007-2013 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * A datagram is a "top-level" container of Ion values, and the granularity of
 * binary encoding Ion content.
 * <p>
 * <b>WARNING:</b> This interface should not be implemented or extended by
 * code outside of this library. Some inherited methods are not yet implemented
 * or are unsupported for datagrams.
 * <p>
 * Along with the normal user values, datagrams contain system values, notably
 * the symbol table(s) used to atomize all symbols. Most uses of a datagram
 * will not see system values,
 * but applications that need visibility into that data can use the
 * <code>system*()</code> methods.
 */
public interface IonDatagram
    extends IonSequence
{
    /**
     * This inherited method is not yet supported by datagrams.
     * <p>
     * Vote for JIRA issue ION-84 if you need this.
     *
     * @throws UnsupportedOperationException at every call.
     *
     * @see <a href="https://jira2.amazon.com/browse/ION-84">ION-84</a>
     */
    public void add(int index, IonValue element)
        throws ContainedValueException, NullPointerException;

    /**
     * This inherited method is not yet supported by datagrams.
     * <p>
     * Vote for JIRA issue ION-84 if you need this.
     *
     * @throws UnsupportedOperationException at every call.
     *
     * @see <a href="https://jira2.amazon.com/browse/ION-84">ION-84</a>
     */
    public ValueFactory add(int index)
        throws ContainedValueException, NullPointerException;

    /**
     * This inherited method is not yet supported by datagrams.
     * <p>
     * Vote for JIRA issue ION-83 if you need this.
     *
     * @throws UnsupportedOperationException at every call.
     *
     * @see <a href="https://jira2.amazon.com/browse/ION-83">ION-83</a>
     */
    public boolean addAll(int index, Collection<? extends IonValue> c);

    /**
     * This inherited method is not yet supported by datagrams.
     * <p>
     * Vote for JIRA issue ION-90 if you need this.
     *
     * @throws UnsupportedOperationException at every call.
     *
     * @see <a href="https://jira2.amazon.com/browse/ION-90">ION-90</a>
     */
    public IonValue set(int index, IonValue element);


    /**
     * Returns {@code false} at every call, since datagrams cannot be null.
     *
     * @return <code>false</code>
     */
    public boolean isNullValue();


    /**
     * {@inheritDoc}
     * <p>
     * Datagrams always return <code>null</code> from this method, since by
     * definition they have no container.
     */
    public IonContainer getContainer();


    /**
     * Gets the number of elements in the datagram, not counting system
     * elements.
     */
    public int size();

    /**
     * Gets the number of elements in the datagram, including system elements
     * such as version markers and symbol tables.
     * Unless your application needs to be aware of such low-level details,
     * you almost certainly want to use {@link #size()} instead.
     *
     * @see #size()
     */
    public int systemSize();


    /**
     * Gets a selected non-system element from this datagram.
     *
     * @param index must be less than <code>{@link #size()}</code>.
     * @return the selected element; not <code>null</code>.
     * @throws IndexOutOfBoundsException if the index is bad.
     */
    public IonValue get(int index)
        throws IndexOutOfBoundsException;

    /**
     * Gets a selected element from this datagram, potentially getting a
     * hidden system element (such as a symbol table).
     * Unless your application needs to be aware of such low-level details,
     * you almost certainly want to use {@link #get(int)} instead.
     *
     * @param index must be less than <code>{@link #systemSize()}</code>.
     * @return the selected element; not <code>null</code>.
     * @throws IndexOutOfBoundsException if the index is bad.
     *
     * @see #get(int)
     */
    public IonValue systemGet(int index)
        throws IndexOutOfBoundsException;


    /**
     * Creates an iterator providing the (direct) elements of this datagram.
     * Elements will be returned
     * in order of their appearance in the Ion representation.
     * <p>
     * This iterator returns only user values, ignoring symbol tables and other
     * system values. It does not support the {@link Iterator#remove()}
     * operation.
     *
     * @see #systemIterator()
     */
    public Iterator<IonValue> iterator();

    /**
     * Iterate all values in the datagram, including the otherwise-hidden
     * system values such as version markers and symbol tables.
     * Unless your application needs to be aware of such low-level details,
     * you almost certainly want to use {@link #iterator()} instead.
     * <p>
     * This iterator does not support the modification methods
     * {@link Iterator#remove()}, {@link ListIterator#add(Object)}, or
     * {@link ListIterator#set(Object)}.
     *
     * @return not null.
     *
     * @see #iterator()
     */
    public ListIterator<IonValue> systemIterator();


    /**
     * Gets the number of bytes used to encode this datagram.
     * As a side effect, this method encodes the entire datagram into Ion
     * binary format.

     * @return the number of bytes in the binary encoding of this datagram.
     *
     * @throws IonException if there's an error encoding the data.
     */
    public int byteSize()
        throws IonException;


    /**
     * Copies the binary-encoded form of this datagram into a new byte array.
     *
     * @return a new, non-empty byte array containing the encoded datagram.
     *
     * @throws IonException if there's an error encoding the data.
     *
     * @deprecated Since IonJava RC1 (2009). Use {@link #getBytes()} instead,
     * which is renamed for consistency with other interfaces.
     */
    @Deprecated
    public byte[] toBytes()
        throws IonException;

    /**
     * Copies the binary-encoded form of this datagram into a new byte array.
     *
     * @return a new, non-empty byte array containing the encoded datagram.
     *
     * @throws IonException if there's an error encoding the data.
     */
    public byte[] getBytes()
        throws IonException;


    /**
     * Copies the binary-encoded form of this datagram into a given array.
     * <p>
     * The given array must be large enough to contain all the bytes of this
     * datagram.
     * <p>
     * An invocation of this method of the form {@code dg.get(a)} behaves in
     * exactly the same way as the invocation:
     * <pre>
     *    dg.get(a, 0)
     * </pre>
     *
     * @param dst the array into which bytes are to be written.
     *
     * @return the number of bytes copied into {@code dst}.
     *
     * @throws IonException if there's an error encoding the data.
     * @throws IndexOutOfBoundsException if {@code dst.length} is
     * smaller than the result of {@link #byteSize()}.
     *
     * @see #getBytes(byte[],int)
     *
     * @deprecated Since IonJava R17, with no direct replacement.
     */
    @Deprecated
    public int getBytes(byte[] dst) // TODO ION-365 Remove
        throws IonException;


    /**
     * Copies the binary-encoded form of this datagram into a given sub-array.
     * <p>
     * The given subarray must be large enough to contain all the bytes of this
     * datagram.
     *
     * @param dst the array into which bytes are to be written.
     * @param offset the offset within the array of the first byte to be
     *   written; must be non-negative and no larger than {@code dst.length}
     *
     * @return the number of bytes copied into {@code dst}.
     *
     * @throws IonException if there's an error encoding the data.
     * @throws IndexOutOfBoundsException if {@code (dst.length - offset)} is
     * smaller than the result of {@link #byteSize()}.
     *
     * @deprecated Since IonJava R17, with no direct replacement.
     */
    @Deprecated
    public int getBytes(byte[] dst, int offset) // TODO ION-365 Remove
        throws IonException;


    /**
     * Copies the binary-encoded form of this datagram to a specified stream.
     *
     * @param out the output stream to which to write the data.
     *
     * @return the number of bytes written.
     *
     * @throws IonException if there's an error encoding the data.
     * @throws IOException if an error occurs writing the data to the stream.
     */
    public int getBytes(OutputStream out)
        throws IOException, IonException;


    /**
     * This inherited method is not supported by datagrams because there's no
     * single symbol table used across the contents.  Each value contained by
     * the datagram may have its own symbol table.
     *
     * @return nothing, always throws an exception.
     *
     * @throws UnsupportedOperationException at every call.
     */
    public SymbolTable getSymbolTable();

    /**
     * This inherited method is not supported by datagrams.
     *
     * @throws UnsupportedOperationException at every call.
     */
    public void addTypeAnnotation(String annotation);

    /**
     * This inherited method is not supported by datagrams.
     *
     * @throws UnsupportedOperationException at every call.
     */
    public void makeNull();


    /**
     * This inherited method is not yet supported by datagrams.
     * <p>
     * Vote for JIRA issue ION-85 if you need this.
     *
     * @throws UnsupportedOperationException at every call.
     *
     * @see <a href="https://jira2.amazon.com/browse/ION-85">ION-85</a>
     */
    public boolean retainAll(Collection<?> c);


    public IonDatagram clone()
        throws UnknownSymbolException;
}
